# IBE — Guide de Refactoring BACKEND (v3 — complet, A à Z)
> Spring Boot 3.3 · Java 17 · PostgreSQL · Flyway · Docker · DDD allégé + Hexagonal
> 3 environnements : Local (Xubuntu+Docker) · Staging (Render) · Production (VPS)
> Institut Beauty's Empire — Douala, Cameroun

---

## 0. Les 3 environnements — logique générale

| Environnement | Usage | Stack |
|---|---|---|
| **Local** | Développement quotidien sur ton PC Xubuntu | Docker Compose : Spring Boot + PostgreSQL + pgAdmin |
| **Staging (Render/Vercel)** | Tests rapides, démo à des tiers, avant le vrai lancement | Render (backend+DB managée) + Vercel (frontend) — gratuit, simple, mais cold start |
| **Production (VPS)** | Le vrai site, en ligne en continu | VPS (Docker + Nginx + Certbot SSL), domaine réel, aucun cold start |

Tu codes une seule fois, tu déploies sur les trois avec des `application-{profile}.properties` différents. Aucune logique métier ne change selon l'environnement — seulement la config.

---

## 1. Diagnostic résumé (bugs à corriger en premier)

| # | Bug | Fichier | Correction |
|---|---|---|---|
| 1 | Import erroné `java.nio.file.AccessDeniedException` | `GlobalExceptionHandler` | Remplacer par `com.example.institue1.exception.AccessDeniedException` |
| 2 | Deux `FormationSimpleDto` en conflit | `dto/formation/` et `dto/contactInscription/` | Fusionner en un seul DTO (+ champ `placesRestantes`) |
| 3 | `MessageMapper` avec `uses = {FormationSimpleDto.class}` | `MessageMapper` | Créer `FormationSimpleDtoMapper` dédié |
| 4 | `FormationInitializer` revérifie nom par nom | `FormationInitializer` | `if (count() == 0)` + `@Profile("!prod")` |
| 5 | Sécurité trop permissive | `SecurityConfig` | Protéger écriture + `/api/admin/**` par `ROLE_ADMIN` |
| 6 | Stack email complète inutile | `EmailService`, Thymeleaf | Supprimer, remplacer par liens WhatsApp |
| 7 | Pas de téléphone sur `NewsletterSubscription` | `model/` | Ajouter `telephone` (obligatoire) + `contacte` (boolean) |

---

## 2. Fonctionnalités attendues — institut de beauté Cameroun

**Déjà couvert par le modèle actuel** : catalogue formations FCFA, social proof, packs combinés, galerie.

**Ajouté dans cette v3 suite aux retours** :
- Dates de prochaine session par formation (la question n°1 d'un prospect)
- Page FAQ, page À propos, page Localisation, infos de paiement Mobile Money
- robots.txt + sitemap.xml pour le SEO
- Analytics léger (Plausible)

---

## 3. Architecture DDD allégée + Hexagonale (rappel condensé)

```
src/main/java/com/example/institue1/
├── domain/{formation, message, shared}/        # entités pures, zéro Spring/JPA, interfaces Port
├── application/{formation, message, notification}/  # use cases, NotificationPort
├── infrastructure/{persistence, config, seed}/  # adapters JPA, SecurityConfig, seed
└── presentation/{api, admin, dto, mapper}/      # controllers REST, DTOs, MapStruct
```
Règle d'or : `domain/` n'importe jamais `jakarta.persistence.*` ni `org.springframework.*`. Appliquer au minimum sur `Formation` et `Message`.

---

## 4. Confirmation WhatsApp côté utilisateur

```java
// application/notification/WhatsAppLinkGenerator.java
@Component
public class WhatsAppLinkGenerator {

    @Value("${app.whatsapp.admin-number}")
    private String adminNumber;

    public String genererLienAdmin(Message message) {
        String texte = String.format(
            "🔔 Nouvelle %s IBE%n👤 %s%n📱 %s%n📚 %s%n🏙️ %s%n💬 %s",
            message.getType().getLibelle(), message.getNom(), message.getTelephone(),
            message.getFormationNom() != null ? message.getFormationNom() : "Contact général",
            message.getVille() != null ? message.getVille() : "Non renseigné",
            message.getMessage());
        return construireLien(adminNumber, texte);
    }

    public String genererLienConfirmationUtilisateur(Message message) {
        String texte = message.getType() == TypeMessage.PRE_INSCRIPTION
            ? String.format("Bonjour IBE 😊 Je confirme ma pré-inscription à la formation *%s*. Mon nom : %s.",
                message.getFormationNom(), message.getNom())
            : String.format("Bonjour IBE 😊 Suite à mon message sur le site (%s), mon nom : %s.",
                message.getSujet() != null ? message.getSujet() : "demande d'info", message.getNom());
        return construireLien(adminNumber, texte);
    }

    private String construireLien(String phone, String texte) {
        String clean = phone.replaceAll("[^0-9]", "");
        String num = clean.startsWith("237") ? clean : "237" + clean;
        return "https://wa.me/" + num + "?text=" + URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }
}
```
`MessageDetailDto` reçoit un champ `whatsappConfirmationLink` rempli dans `MessageService.creerPreInscription()` et `creerContact()`. Le frontend affiche un bouton avec ce lien sur l'écran de succès.

---

## 5. Validation téléphone camerounais

```java
@NotBlank(message = "Le numéro de téléphone est obligatoire")
@Pattern(regexp = "^(\\+?237)?6[5-9]\\d{7}$", message = "Numéro camerounais invalide — format : 6XX XXX XXX")
private String telephone;
```
Normaliser le stockage en base au format `2376XXXXXXXX` dans `MessageUtils.formaterTelephone()`. Appliquer sur `ContactCreateDto`, `PreInscriptionCreateDto`, `NewsletterSubscribeDto`.

---

## 6. Sessions de formation — champ manquant critique

```java
// model/Formation.java — ajouter
@Column
private LocalDate dateDemarrage;

@Column
private LocalDate dateFinInscription;

@Column(length = 100)
private String joursFormation; // "Lundi, Mercredi, Vendredi"
```
```java
// FormationDetailDto, FormationListDto — ajouter les 3 mêmes champs
// FormationController — nouvel endpoint
@GetMapping("/{id}/prochaine-session")
public ResponseEntity<ApiResponse<Map<String, Object>>> getProchaineSession(@PathVariable Long id) {
    return formationService.getFormationById(id, false)
        .map(f -> ResponseEntity.ok(ApiResponse.success(Map.of(
            "dateDemarrage", f.getDateDemarrage(),
            "dateFinInscription", f.getDateFinInscription(),
            "joursFormation", f.getJoursFormation(),
            "placesRestantes", f.getPlacesRestantes()
        ))))
        .orElseThrow(() -> new FormationNotFoundException(id));
}
```
Ces 3 champs sont éditables depuis le dashboard admin (formulaire formation), et affichés en évidence sur la page détail formation côté frontend.

---

## 7. Flyway — migrations versionnées

```xml
<dependency><groupId>org.flywaydb</groupId><artifactId>flyway-core</artifactId></dependency>
<dependency><groupId>org.flywaydb</groupId><artifactId>flyway-database-postgresql</artifactId></dependency>
```
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```
```
db/migration/
├── V1__init_schema.sql              # tables formations, messages, gallery_images, newsletter
├── V2__add_telephone_newsletter.sql # + contacte boolean
├── V3__add_version_formation.sql    # verrou optimiste
├── V4__create_testimonials.sql
├── V5__add_session_dates_formation.sql  # dateDemarrage, dateFinInscription, joursFormation
└── V6__create_partners.sql          # si entité Partner choisie (voir section 12)
```
Règle : une migration poussée en prod n'est jamais modifiée, on en crée une nouvelle.

---

## 8. Rate limiting — avec ordre d'exécution correct

```xml
<dependency><groupId>com.bucket4j</groupId><artifactId>bucket4j-core</artifactId><version>8.10.1</version></dependency>
```
```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // s'exécute AVANT Spring Security
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final List<String> PROTEGES = List.of(
        "/api/messages/contact", "/api/messages/pre-inscription", "/api/newsletter/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (PROTEGES.stream().anyMatch(req.getRequestURI()::startsWith)) {
            Bucket bucket = buckets.computeIfAbsent(req.getRemoteAddr(), k ->
                Bucket.builder().addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)))).build());
            if (!bucket.tryConsume(1)) {
                res.setStatus(429);
                res.setContentType("application/json");
                res.getWriter().write("{\"success\":false,\"error\":\"Trop de requêtes, réessayez dans quelques minutes\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
```
```java
// infrastructure/config/FilterConfig.java — registration EXPLICITE avant la chaîne Security
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}
```

---

## 9. Verrou optimiste — avec @EnableRetry correctement posé

```java
// Institue1Application.java — sur la classe principale
@SpringBootApplication
@EnableRetry   // ← OBLIGATOIRE pour que @Retryable fonctionne
public class Institue1Application {
    public static void main(String[] args) { SpringApplication.run(Institue1Application.class, args); }
}
```
```xml
<dependency><groupId>org.springframework.retry</groupId><artifactId>spring-retry</artifactId></dependency>
```
```java
// model/Formation.java
@Version
@Column(nullable = false)
private Long version = 0L;
```
```java
// service/FormationService.java
@Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
public boolean recordEnrollment(Long formationId) {
    return formationRepository.findById(formationId)
        .map(f -> {
            if (!formationUtils.peutSInscrire(f)) return false;
            formationUtils.ajouterInscriptionReelle(f);
            formationRepository.save(f);
            return true;
        }).orElse(false);
}
```

---

## 10. Upload Cloudinary — suppression synchronisée (fix critique)

> Le bug signalé : supprimer une image sur Cloudinary sans supprimer la ligne en base laisse des `gallery_images` orphelines pointant vers des URLs mortes. La suppression doit toujours se faire **dans le même service transactionnel**.

```java
@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.url}") private String cloudinaryUrl;
    @Bean public Cloudinary cloudinary() { return new Cloudinary(cloudinaryUrl); }
}
```
```java
// service/GalleryImageService.java — méthode de suppression corrigée
@Transactional
public boolean deleteImage(Long id) {
    GalleryImage image = galleryImageRepository.findById(id).orElse(null);
    if (image == null) return false;

    try {
        String publicId = extrairePublicId(image.getUrl()); // parse l'URL Cloudinary
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (Exception e) {
        log.error("Erreur suppression Cloudinary pour image {} : {}", id, e.getMessage());
        // On continue quand même la suppression DB — mieux vaut une ligne en moins
        // qu'une image fantôme jamais nettoyée. Logger pour audit manuel si besoin.
    }

    galleryImageRepository.deleteById(id); // les deux opérations dans la même transaction
    return true;
}

private String extrairePublicId(String url) {
    // ex: https://res.cloudinary.com/.../ibe/formations/abc123.jpg → ibe/formations/abc123
    String sansExtension = url.substring(0, url.lastIndexOf('.'));
    return sansExtension.substring(sansExtension.indexOf("/upload/") + 8)
                         .replaceFirst("v\\d+/", ""); // retire le préfixe de version Cloudinary
}
```
```java
// controller/UploadController.java — utilise ce service au lieu d'appeler Cloudinary directement
@DeleteMapping("/image/{id}")
public ResponseEntity<ApiResponse<Object>> deleteImage(@PathVariable Long id) {
    boolean deleted = galleryImageService.deleteImage(id); // CDN + DB ensemble
    return deleted ? ResponseEntity.ok(ApiResponse.success("Image supprimée"))
                   : ResponseEntity.notFound().build();
}
```
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
cloudinary.url=${CLOUDINARY_URL}
```
Upload avec compression auto : `ObjectUtils.asMap("folder", "ibe/formations", "quality", "auto:good", "fetch_format", "auto")`.

---

## 11. Authentification admin

```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
        User.builder().username("${app.admin.username}")
            .password(encoder.encode("${app.admin.password}")).roles("ADMIN").build());
}

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults())
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(HttpMethod.GET, "/api/formations/**", "/api/gallery/**", "/api/testimonials").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/messages/**", "/api/newsletter/subscribe").permitAll()
            .requestMatchers("/actuator/health").permitAll()  // pour le ping anti-cold-start
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().hasRole("ADMIN"));
    return http.build();
}
```
Côté Angular : login form → Base64 → header `Authorization: Basic xxx` stocké en signal mémoire (jamais localStorage en clair).

---

## 12. Entités Testimonial et Partner

```java
@Entity @Table(name = "testimonials")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Testimonial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String nomEtudiant;
    @Column(nullable = false, length = 100) private String formationSuivie;
    @Column(columnDefinition = "TEXT", nullable = false) private String temoignage;
    @Min(1) @Max(5) private Integer note;
    private String photoUrl;
    @Column(nullable = false) private Boolean publie = false;
    @Column(nullable = false, updatable = false) private LocalDateTime dateCreation = LocalDateTime.now();
}
```
`GET /api/testimonials` (public, `publie=true` uniquement), `GET /api/testimonials/admin` (tous), `POST /api/testimonials` (admin), `PATCH /api/testimonials/{id}/publier` (admin, toggle).

**Partner (logos partenaires/MINEFOP)** : changent rarement → reste en JSON statique côté frontend (`assets/data/partners.json`), pas besoin d'entité backend pour ça. Pas de `V6__create_partners.sql` nécessaire, simplification volontaire.

---

## 13. SEO technique — robots.txt + Actuator

```
# src/main/resources/static/robots.txt
User-agent: *
Disallow: /admin
Allow: /
Sitemap: https://ibe-douala.com/sitemap.xml
```
(le `sitemap.xml` est généré côté frontend au build — voir guide frontend)

```xml
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
```
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
```
`GET /actuator/health` sert de cible pour le ping anti-cold-start sur Render (section 15) et pour la supervision sur le VPS.

---

## 14. Scénarios d'erreur — comportement attendu

| Scénario | Comportement backend |
|---|---|
| Deux inscriptions simultanées, dernière place | `@Version` lève `ObjectOptimisticLockingFailureException`, retry 3x, sinon réponse `false` → frontend affiche "Formation complète" |
| Spam formulaire (6e tentative en 10 min, même IP) | `429 Too Many Requests` avec message JSON clair |
| Téléphone invalide ("123456") | `400` avec message de validation — mais le frontend doit déjà bloquer ça côté client avant l'appel réseau |
| Suppression image Cloudinary échoue mais DB doit être nettoyée | Log l'erreur, supprime quand même la ligne DB (voir section 10) |
| Backend en veille (Render cold start) | `GET /actuator/health` répond une fois réveillé — voir stratégie anti-cold-start section 15 |

---

## 15. Déploiement — les 3 environnements en détail

### 15.1 Local — Xubuntu + Docker (développement quotidien)

```yaml
# docker-compose.yml — racine du projet backend
version: '3.8'
services:
  backend:
    build: .
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DATABASE_URL: jdbc:postgresql://db:5432/ibe_db
      DATABASE_USERNAME: ibe_user
      DATABASE_PASSWORD: ibe_dev_pass
    depends_on:
      db: { condition: service_healthy }

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ibe_db
      POSTGRES_USER: ibe_user
      POSTGRES_PASSWORD: ibe_dev_pass
    volumes: ["postgres_data:/var/lib/postgresql/data"]
    ports: ["5432:5432"]
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ibe_user -d ibe_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  pgadmin:
    image: dpage/pgadmin4:latest
    ports: ["5050:80"]
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@ibe.local
      PGADMIN_DEFAULT_PASSWORD: admin

volumes:
  postgres_data:
```
```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
Commandes : `docker compose up -d` (tout démarre), `docker compose logs -f backend` (logs en direct). Flyway crée les tables automatiquement au premier démarrage. pgAdmin accessible sur `localhost:5050` pour inspecter la base visuellement.

### 15.2 Staging — Render + Vercel (tests rapides, démo)

```properties
# application-staging.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
app.whatsapp.admin-number=${WHATSAPP_ADMIN_NUMBER}
app.admin.username=${ADMIN_USERNAME}
app.admin.password=${ADMIN_PASSWORD}
cloudinary.url=${CLOUDINARY_URL}
app.cors.allowed-origins=${FRONTEND_URL}
```

**Problème critique du free tier** : Render endort le backend après 15 min d'inactivité. Premier visiteur après la pause attend 30-60s. Trois solutions, à combiner si besoin :
1. **Banner de chargement côté Angular** (le plus simple, voir guide frontend section "ping anti-cold-start")
2. **Cron externe gratuit** (cron-job.org) qui ping `GET /actuator/health` toutes les 10 minutes pour garder le backend éveillé
3. **Tier payant Render** (~7$/mois) si le budget le permet — supprime complètement le problème

> Recommandation : utiliser Render/Vercel uniquement comme **environnement de staging/démo**, pas comme la production finale — le VPS (section 15.3) est la vraie cible de production, donc le cold start n'affecte jamais les vrais visiteurs.

### 15.3 Production — VPS (la cible finale)

Sur le VPS : Docker + Docker Compose + Nginx + Certbot.

```nginx
# /etc/nginx/sites-available/ibe
server {
    listen 80;
    server_name ton-domaine.com www.ton-domaine.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name ton-domaine.com www.ton-domaine.com;
    ssl_certificate /etc/letsencrypt/live/ton-domaine.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/ton-domaine.com/privkey.pem;

    root /var/www/ibe-frontend/browser;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;  # routes Angular SPA
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 60s;
    }
}
```
SSL en une commande : `sudo certbot --nginx -d ton-domaine.com` (renouvellement automatique).

```yaml
# docker-compose.prod.yml — sur le VPS
version: '3.8'
services:
  backend:
    build: .
    restart: unless-stopped
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: ${DATABASE_URL}
      DATABASE_USERNAME: ${DATABASE_USERNAME}
      DATABASE_PASSWORD: ${DATABASE_PASSWORD}
      WHATSAPP_ADMIN_NUMBER: ${WHATSAPP_ADMIN_NUMBER}
      ADMIN_USERNAME: ${ADMIN_USERNAME}
      ADMIN_PASSWORD: ${ADMIN_PASSWORD}
      CLOUDINARY_URL: ${CLOUDINARY_URL}
      FRONTEND_URL: ${FRONTEND_URL}
    depends_on: [db]

  db:
    image: postgres:16-alpine
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes: ["postgres_data_prod:/var/lib/postgresql/data"]

volumes:
  postgres_data_prod:
```

```yaml
# .github/workflows/deploy.yml — CI/CD GitHub Actions
name: Deploy to VPS
on:
  push: { branches: [main] }
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build Angular
        working-directory: ./frontend
        run: |
          npm ci
          npm run build -- --configuration production
      - name: Deploy to VPS
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.VPS_HOST }}
          username: ${{ secrets.VPS_USER }}
          key: ${{ secrets.VPS_SSH_KEY }}
          script: |
            cd /opt/ibe
            git pull origin main
            docker compose -f docker-compose.prod.yml up -d --build backend
            cp -r /opt/ibe/frontend/dist/ibe-frontend/browser/* /var/www/ibe-frontend/browser/
            sudo nginx -s reload
```

```bash
# Backup PostgreSQL — crontab -e sur le VPS
0 2 * * * docker exec ibe-db pg_dump -U ibe_user ibe_db > /opt/backups/ibe_$(date +\%Y\%m\%d).sql
find /opt/backups -name "*.sql" -mtime +30 -delete
```

---

## 16. PROMPT À COPIER DANS AI STUDIO (v3 — complet)

```
Tu es un expert Spring Boot / Java 17 / PostgreSQL / Docker / architecture DDD-hexagonale. Je te fournis le code source complet d'un backend Spring Boot existant pour un institut de beauté au Cameroun (Institut Beauty's Empire). Refactorise-le ENTIÈREMENT. Donne-moi le code complet, fichier par fichier, prêt à coller.

CONTEXTE MÉTIER :
- Institut de formation beauté à Douala. Conversion via WhatsApp uniquement (jamais email). Téléphone obligatoire, email optionnel.
- Prix en FCFA. Social proof sur les formations. PostgreSQL uniquement (retire mysql-connector-j du pom).
- 3 environnements cibles : local (Docker Compose), staging (Render), production (VPS avec Nginx + Docker)

CORRECTIONS BUGS :
1. GlobalExceptionHandler : import AccessDeniedException erroné (java.nio.file → com.example.institue1.exception)
2. Fusionne les deux FormationSimpleDto en un seul (+ placesRestantes)
3. MessageMapper : crée FormationSimpleDtoMapper dédié pour le uses
4. FormationInitializer : if (count()==0) + @Profile("!prod")
5. Sécurise POST/PUT/DELETE /api/formations et tout /api/admin/** derrière ROLE_ADMIN

WHATSAPP-FIRST COMPLET :
- Supprime EmailService, Thymeleaf, spring-boot-starter-mail
- WhatsAppLinkGenerator avec genererLienAdmin(Message) ET genererLienConfirmationUtilisateur(Message) — ce dernier doit remplir le champ whatsappConfirmationLink de MessageDetailDto retourné par POST /api/messages/contact et POST /api/messages/pre-inscription (l'utilisateur clique ce lien pour confirmer lui-même sa demande sur WhatsApp, jamais d'envoi automatique serveur)
- NewsletterSubscription : champs telephone (obligatoire) + contacte (boolean)

VALIDATION TÉLÉPHONE : pattern ^(\+?237)?6[5-9]\d{7}$ sur tous les champs telephone, normalisation en base au format 237XXXXXXXXX.

SESSIONS DE FORMATION (nouveau, important) : ajoute à Formation les champs dateDemarrage (LocalDate), dateFinInscription (LocalDate), joursFormation (String, ex "Lundi, Mercredi, Vendredi"). Ajoute-les à FormationListDto, FormationDetailDto, FormationCreateDto, FormationUpdateDto. Crée GET /api/formations/{id}/prochaine-session qui retourne ces infos + placesRestantes.

FLYWAY : flyway-core + flyway-database-postgresql, ddl-auto=validate, migrations V1 (schéma initial) à V5 (ajout colonnes session sur formations), V4 (table testimonials).

RATE LIMITING avec ordre d'exécution correct : bucket4j-core, filtre OncePerRequestFilter avec @Order(Ordered.HIGHEST_PRECEDENCE), ET registration explicite via FilterRegistrationBean dans une classe @Configuration pour garantir qu'il s'exécute AVANT Spring Security. Limite 5 req/10min par IP sur /api/messages/contact, /api/messages/pre-inscription, /api/newsletter/subscribe. Réponse 429 JSON claire.

VERROU OPTIMISTE avec placement correct : @Version sur Formation.version. IMPORTANT : ajoute @EnableRetry sur la classe principale @SpringBootApplication (Institue1Application), pas seulement spring-retry en dépendance — sans cette annotation @Retryable est ignoré silencieusement. @Retryable(retryFor=ObjectOptimisticLockingFailureException.class, maxAttempts=3, backoff=@Backoff(delay=100)) sur FormationService.recordEnrollment().

UPLOAD CLOUDINARY avec suppression synchronisée (fix critique) : CloudinaryConfig + endpoint upload avec quality:auto:good et fetch_format:auto. Pour la suppression : GalleryImageService.deleteImage(id) doit supprimer l'image sur Cloudinary CloudinaryET la ligne en base gallery_images DANS LA MÊME méthode @Transactional — jamais l'un sans l'autre. Si la suppression Cloudinary échoue (exception catchée), logger l'erreur mais supprimer quand même la ligne DB plutôt que de laisser un état incohérent. spring.servlet.multipart.max-file-size=10MB.

AUTHENTIFICATION ADMIN : InMemoryUserDetailsManager un seul compte (variables ADMIN_USERNAME/ADMIN_PASSWORD), httpBasic(), stateless, CORS avec FRONTEND_URL en variable d'environnement. Expose /actuator/health publiquement (pour le ping anti-cold-start).

ENTITÉ TESTIMONIAL : nomEtudiant, formationSuivie, temoignage, note(1-5), photoUrl, publie(boolean), dateCreation. GET /api/testimonials (public, publie=true), GET /api/testimonials/admin (tous), POST et PATCH /{id}/publier (admin).

SEO BACKEND : ajoute src/main/resources/static/robots.txt (Disallow: /admin, Allow: /, référence au sitemap).

ACTUATOR : spring-boot-starter-actuator, expose uniquement /actuator/health, show-details=never.

ARCHITECTURE DDD/HEXAGONALE (Formation et Message au minimum) :
domain/ (entités pures) → application/ (use cases) → infrastructure/ (adapters, config) → presentation/ (controllers, DTOs, mappers)

DOCKER LOCAL : crée docker-compose.yml (backend + postgres:16-alpine + pgadmin avec healthcheck) et Dockerfile multi-stage (build Maven puis JRE alpine léger).

DÉPLOIEMENT VPS : crée docker-compose.prod.yml (backend + postgres, restart unless-stopped, toutes les variables sensibles via env), un exemple de config Nginx (reverse proxy /api/ vers le backend, sert le frontend Angular buildé en SPA avec try_files), et un script crontab de backup PostgreSQL quotidien avec rétention 30 jours.

CI/CD : crée .github/workflows/deploy.yml qui build Angular, puis se connecte en SSH au VPS pour git pull + docker compose up -d --build + copie le build frontend + reload Nginx.

CONFIGS : application.properties (commun), application-dev.properties (Docker local), application-staging.properties (Render, variables d'env), application-prod.properties (VPS, variables d'env).

POM.XML FINAL : retire spring-boot-starter-mail, spring-boot-starter-thymeleaf, mysql-connector-j. Ajoute flyway-core, flyway-database-postgresql, bucket4j-core, spring-retry, cloudinary-http5, spring-boot-starter-actuator.

LIVRABLE, DANS CET ORDRE :
1. Arborescence complète
2. pom.xml
3. Migrations Flyway V1 à V5
4. domain/ → application/ → infrastructure/ → presentation/
5. docker-compose.yml, Dockerfile, docker-compose.prod.yml, nginx.conf exemple, deploy.yml
6. application*.properties (les 4 variantes)

[COLLE ICI TOUT LE CODE SOURCE BACKEND ACTUEL]
```

---

*v3 — intègre Docker local, VPS production complet (Nginx/Certbot/CI-CD/backup), stratégie staging Render avec mitigation cold start, dates de session formation, fix suppression Cloudinary synchronisée, ordre d'exécution rate limiter, placement @EnableRetry.*


# IBE — Guide de Refactoring BACKEND (v2 — complet)
> Spring Boot 3.3 · Java 17 · PostgreSQL · Flyway · DDD allégé + Hexagonal · WhatsApp-first
> Déploiement cible : Render
> Institut Beauty's Empire — Douala, Cameroun

---

## 1. Ce qui a changé depuis la v1

| Ajout | Pourquoi |
|---|---|
| Confirmation WhatsApp **côté utilisateur** | Sans email, l'utilisateur n'avait aucune preuve que sa demande était reçue |
| Regex téléphone camerounais strict | L'ancien pattern acceptait n'importe quel numéro mondial |
| Flyway | `ddl-auto=update` en prod = risque de perte de données |
| Rate limiting (Bucket4j) | Les 3 endpoints publics d'écriture étaient ouverts au spam |
| Verrou optimiste sur les places | Race condition possible si 2 inscriptions simultanées sur la dernière place |
| Upload Cloudinary complet | Dépendance ajoutée en v1 mais jamais implémentée |
| Auth admin détaillée | Le lien entre backend et Angular n'était pas spécifié |
| Entité Testimonial | Les témoignages affichés en frontend n'avaient pas de source de données |
| `application-prod.properties` pour Render + PostgreSQL | Décision prise : PostgreSQL only, hébergement Render |

Tout le reste (bugs 1-5 de la v1, architecture DDD/hexagonale, WhatsApp anti-spam) reste valable, voir en bas du document pour le rappel condensé.

---

## 2. Confirmation WhatsApp côté utilisateur (le point critique manquant)

### Flux complet
```
1. Utilisateur remplit le formulaire de pré-inscription/contact
2. Frontend → POST /api/messages/pre-inscription
3. Backend sauvegarde en base (dashboard admin alimenté)
4. Backend génère DEUX liens WhatsApp et les retourne dans la réponse :
   - whatsappConfirmationLink : message orienté UTILISATEUR → ADMIN
     ("Bonjour, je confirme ma pré-inscription à la formation Onglerie...")
   - (le lien admin reste interne, généré séparément, visible uniquement dans le dashboard)
5. Frontend affiche l'écran de succès avec le bouton
   "✅ Pré-inscription enregistrée — Confirmez sur WhatsApp →"
6. L'utilisateur clique → WhatsApp s'ouvre chez LUI avec le message pré-rempli → il l'envoie
```

### Implémentation
```java
// dto/contactInscription/MessageDetailDto.java — ajouter
private String whatsappConfirmationLink;

// service/MessageService.java — dans creerPreInscription() et creerContact()
MessageDetailDto dto = messageMapper.toDetailDto(saved);
dto.setWhatsappConfirmationLink(whatsAppLinkGenerator.genererLienConfirmationUtilisateur(saved));
return dto;
```

```java
// application/notification/WhatsAppLinkGenerator.java
@Component
public class WhatsAppLinkGenerator {

    @Value("${app.whatsapp.admin-number}")
    private String adminNumber;

    /** Lien pour l'ADMIN (affiché dans le dashboard) */
    public String genererLienAdmin(Message message) {
        String texte = String.format(
            "🔔 Nouvelle %s IBE%n👤 %s%n📱 %s%n📚 %s%n🏙️ %s%n💬 %s",
            message.getType().getLibelle(), message.getNom(), message.getTelephone(),
            message.getFormationNom() != null ? message.getFormationNom() : "Contact général",
            message.getVille() != null ? message.getVille() : "Non renseigné",
            message.getMessage()
        );
        return construireLien(adminNumber, texte);
    }

    /** Lien pour l'UTILISATEUR — il envoie LUI-MÊME ce message vers l'admin pour confirmer */
    public String genererLienConfirmationUtilisateur(Message message) {
        String texte = message.getType() == TypeMessage.PRE_INSCRIPTION
            ? String.format(
                "Bonjour IBE 😊 Je confirme ma pré-inscription à la formation *%s*. Mon nom : %s. Merci de me recontacter pour la suite !",
                message.getFormationNom(), message.getNom())
            : String.format(
                "Bonjour IBE 😊 Je viens de vous envoyer un message via le site (%s). Mon nom : %s.",
                message.getSujet() != null ? message.getSujet() : "demande d'info", message.getNom());
        return construireLien(adminNumber, texte);
    }

    private String construireLien(String phone, String texte) {
        String clean = phone.replaceAll("[^0-9]", "");
        String num = clean.startsWith("237") ? clean : "237" + clean;
        return "https://wa.me/" + num + "?text=" + URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }
}
```

---

## 3. Validation téléphone camerounais

Les numéros mobiles camerounais : 9 chiffres commençant par `6` (MTN, Orange, Camtel, Nexttel), précédés ou non de l'indicatif `+237`.

```java
// À utiliser dans TOUS les DTOs avec un champ telephone
@NotBlank(message = "Le numéro de téléphone est obligatoire")
@Pattern(
    regexp = "^(\\+?237)?6[5-9]\\d{7}$",
    message = "Numéro camerounais invalide — format attendu : 6XX XXX XXX ou +237 6XX XXX XXX"
)
private String telephone;
```

> Note : `6[5-9]` couvre les préfixes actuels (650-699). Si de nouveaux préfixes opérateurs apparaissent, élargir à `6\d{8}` en restant souple plutôt que bloquer des inscriptions légitimes.

Mettre à jour : `ContactCreateDto`, `PreInscriptionCreateDto`, et le formatage dans `MessageUtils.formaterTelephone()` pour toujours stocker au format `2376XXXXXXXX` (normalisé, sans espaces) en base — ça simplifie la génération des liens `wa.me` partout.

---

## 4. Flyway — migrations versionnées

### pom.xml
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

### application.properties (commun)
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### Structure des migrations
```
src/main/resources/db/migration/
├── V1__init_schema.sql           # Tables formations, messages, gallery_images, newsletter
├── V2__add_telephone_newsletter.sql
├── V3__add_version_formation.sql # verrou optimiste (section 6)
├── V4__create_testimonials.sql
└── V5__seed_admin_user.sql
```

```sql
-- V2__add_telephone_newsletter.sql
ALTER TABLE newsletter_subscriptions ADD COLUMN telephone VARCHAR(20);
ALTER TABLE newsletter_subscriptions ADD COLUMN contacte BOOLEAN NOT NULL DEFAULT false;
```

> Règle : une fois une migration `Vx__` poussée en prod, on ne la modifie jamais — on en crée une nouvelle. Sinon Flyway refuse de démarrer (checksum mismatch).

---

## 5. Rate limiting (Bucket4j)

### pom.xml
```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

### Filtre simple en mémoire (pas besoin de Redis pour ce volume)
```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final List<String> ENDPOINTS_PROTEGES = List.of(
        "/api/messages/contact", "/api/messages/pre-inscription", "/api/newsletter/subscribe"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        boolean estProtege = ENDPOINTS_PROTEGES.stream().anyMatch(req.getRequestURI()::startsWith);

        if (estProtege) {
            String ip = req.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, k -> creerBucket());

            if (!bucket.tryConsume(1)) {
                res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                res.getWriter().write("{\"success\":false,\"error\":\"Trop de requêtes, réessayez dans quelques minutes\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private Bucket creerBucket() {
        // 5 requêtes / 10 minutes par IP — large pour un humain, bloque un script
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));
        return Bucket.builder().addLimit(limit).build();
    }
}
```
Enregistrer le filtre dans `SecurityConfig` ou via `@Component` + `FilterRegistrationBean`.

---

## 6. Verrou optimiste sur les places (race condition)

```java
// model/Formation.java — ajouter
@Version
@Column(nullable = false)
private Long version = 0L;
```

```java
// service/FormationService.java — recordEnrollment(), avec retry léger
@Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
public boolean recordEnrollment(Long formationId) {
    return formationRepository.findById(formationId)
        .map(formation -> {
            if (!formationUtils.peutSInscrire(formation)) return false;
            formationUtils.ajouterInscriptionReelle(formation);
            formationRepository.save(formation); // throw ObjectOptimisticLockingFailureException si conflit
            return true;
        })
        .orElse(false);
}
```
Ajouter `spring-retry` + `@EnableRetry` sur la classe principale. Avec `@Version`, si deux requêtes arrivent en même temps sur la dernière place, l'une des deux échoue proprement et retente — jamais de compteur négatif.

---

## 7. Upload d'images — Cloudinary complet

### pom.xml (déjà ajouté en v1, on l'implémente ici)
```xml
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http5</artifactId>
    <version>1.39.0</version>
</dependency>
```

### application.properties
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
cloudinary.url=${CLOUDINARY_URL}
```

### Config + Controller
```java
@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }
}

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final Cloudinary cloudinary;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Le fichier doit être une image"));
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "folder", "ibe/formations",
            "quality", "auto:good",   // compression automatique
            "fetch_format", "auto"    // sert du WebP automatiquement si le navigateur le supporte
        ));

        Map<String, String> data = Map.of(
            "url", uploadResult.get("secure_url").toString(),
            "publicId", uploadResult.get("public_id").toString()
        );

        return ResponseEntity.ok(ApiResponse.success(data, "Image uploadée avec succès"));
    }

    @DeleteMapping("/image/{publicId}")
    public ResponseEntity<ApiResponse<Object>> deleteImage(@PathVariable String publicId) throws Exception {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return ResponseEntity.ok(ApiResponse.success("Image supprimée"));
    }
}
```
`quality: auto:good` + `fetch_format: auto` répondent directement à l'exigence "WebP + < 150 Ko" côté frontend — Cloudinary sert automatiquement le bon format/poids selon le navigateur du visiteur, sans travail supplémentaire.

---

## 8. Authentification Admin — lien backend ↔ frontend

### Approche MVP : token simple, pas de JWT complexe (un seul compte admin)

```java
// infrastructure/config/SecurityConfig.java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails admin = User.builder()
        .username("${app.admin.username}") // résolu depuis application-prod.properties
        .password(encoder.encode("${app.admin.password}"))
        .roles("ADMIN")
        .build();
    return new InMemoryUserDetailsManager(admin);
}

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults()) // simple, fonctionne très bien avec un header Authorization envoyé manuellement
        .authorizeHttpRequests(/* voir v1 section sécurité */);
    return http.build();
}
```

**Côté Angular** (détaillé dans le guide frontend v2) : un formulaire de login custom (pas la popup moche du navigateur), qui encode `username:password` en Base64 et l'envoie comme header `Authorization: Basic xxx` via un interceptor, stocké en mémoire (signal) pendant la session — jamais en `localStorage` en clair.

> Évolution future : si plusieurs admins sont nécessaires, migrer vers JWT avec Spring Security + table `users`. Pas nécessaire pour le MVP à un seul compte.

---

## 9. Entité Testimonial (source de données manquante)

```java
@Entity
@Table(name = "testimonials")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Testimonial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomEtudiant;

    @Column(nullable = false, length = 100)
    private String formationSuivie;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String temoignage;

    @Min(1) @Max(5)
    private Integer note; // étoiles

    private String photoUrl;

    @Column(nullable = false)
    private Boolean publie = false; // l'admin valide avant publication

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}
```

```java
@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    @GetMapping  // public — uniquement les publiés
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> listerPublies() { /* ... */ }

    @PostMapping  // admin — créer
    @GetMapping("/admin")  // admin — voir tous, y compris non publiés
    @PatchMapping("/{id}/publier")  // admin — toggle publication
}
```
Permet à l'admin d'ajouter un témoignage reçu par WhatsApp directement depuis le dashboard, sans toucher au code.

---

## 10. Déploiement — Render + PostgreSQL

### application-prod.properties
```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

spring.flyway.enabled=true

app.whatsapp.admin-number=${WHATSAPP_ADMIN_NUMBER}
app.admin.username=${ADMIN_USERNAME}
app.admin.password=${ADMIN_PASSWORD}
cloudinary.url=${CLOUDINARY_URL}

# CORS — remplacer par le vrai domaine Vercel une fois déployé
app.cors.allowed-origins=${FRONTEND_URL}

logging.level.root=INFO
logging.level.com.example.institue1=INFO
```

### Variables d'environnement à configurer sur Render
```
DATABASE_URL=jdbc:postgresql://...        (auto-rempli par Render si tu lies leur PostgreSQL managé)
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
WHATSAPP_ADMIN_NUMBER=2376XXXXXXXX
ADMIN_USERNAME=admin
ADMIN_PASSWORD=...                         (mot de passe fort, jamais en dur dans le code)
CLOUDINARY_URL=cloudinary://...
FRONTEND_URL=https://ibe-douala.vercel.app
JAVA_OPTS=-Xms256m -Xmx512m                (Render free tier a peu de RAM)
```

### Checklist Render
- [ ] Connecter le repo GitHub, Render détecte le `pom.xml` automatiquement (build Maven)
- [ ] Créer une base PostgreSQL Render (free tier), copier l'URL de connexion interne
- [ ] Renseigner toutes les variables d'environnement ci-dessus dans l'onglet "Environment"
- [ ] Premier déploiement : vérifier les logs Flyway (migrations doivent passer sans erreur)
- [ ] Tester `GET /api/formations` depuis l'URL publique Render avant de connecter le frontend

---

## 11. Rappel condensé des corrections v1 (toujours valables)

- `GlobalExceptionHandler` : corriger l'import `AccessDeniedException` (custom, pas `java.nio.file`)
- Fusionner les deux `FormationSimpleDto` en un seul
- `MessageMapper` : créer un `FormationSimpleDtoMapper` dédié pour le `uses`
- `FormationInitializer` : `if (count() == 0)` + `@Profile("!prod")`
- Sécurité : endpoints d'écriture formations + tout `/api/admin/**` derrière `ROLE_ADMIN`
- Supprimer EmailService, Thymeleaf, `spring-boot-starter-mail` — tout en WhatsApp liens
- `NewsletterSubscription` : champ `telephone` obligatoire + `contacte` (boolean)
- Architecture DDD/hexagonale allégée sur `Formation` et `Message` au minimum

---

## 12. PROMPT À COPIER DANS AI STUDIO (v2 — complet)

```
Tu es un expert Spring Boot / Java 17 / PostgreSQL / architecture DDD-hexagonale. Je te fournis le code source complet d'un backend Spring Boot existant pour un institut de beauté au Cameroun (Institut Beauty's Empire). Refactorise-le ENTIÈREMENT selon les règles ci-dessous. Donne-moi le code complet, fichier par fichier, prêt à coller — pas un résumé.

CONTEXTE MÉTIER :
- Institut de formation beauté à Douala (onglerie, make-up, coiffure, esthétique, cosmétiques, packs, DQP/AQP/CQP)
- Conversion principale via WhatsApp, jamais email. Téléphone obligatoire partout, email optionnel.
- Prix en FCFA. Social proof (inscrits affichés vs réels) sur les formations.
- Base de données : PostgreSQL uniquement (retire complètement le driver MySQL du pom)
- Déploiement cible : Render (backend + PostgreSQL managé)

CORRECTIONS BUGS OBLIGATOIRES :
1. GlobalExceptionHandler importe `java.nio.file.AccessDeniedException` au lieu de `com.example.institue1.exception.AccessDeniedException` — corrige.
2. Deux classes FormationSimpleDto en conflit (dto/formation/ et dto/contactInscription/) — fusionne en une seule avec le champ placesRestantes, supprime le doublon, corrige tous les imports.
3. MessageMapper utilise `uses = {FormationSimpleDto.class}` ce qui est invalide pour MapStruct (uses attend des Mappers). Crée un FormationSimpleDtoMapper dédié.
4. FormationInitializer doit vérifier `if (repository.count() == 0)` au lieu de tester nom par nom, et porter `@Profile("!prod")`.
5. Sécurise tous les endpoints d'écriture (POST/PUT/DELETE /api/formations, tout /api/admin/**) derrière ROLE_ADMIN. Garde public : GET /api/formations, GET /api/gallery, GET /api/testimonials, POST /api/messages/**, POST /api/newsletter/subscribe.

WHATSAPP-FIRST — SUPPRIMER L'EMAIL :
- Supprime EmailService, templates Thymeleaf, dépendances spring-boot-starter-mail et spring-boot-starter-thymeleaf, toutes les properties spring.mail.* et app.email.*
- Crée WhatsAppLinkGenerator avec deux méthodes : genererLienAdmin(Message) pour le dashboard interne, et genererLienConfirmationUtilisateur(Message) qui doit être inclus dans le champ whatsappConfirmationLink de MessageDetailDto retourné par les endpoints POST /api/messages/contact et POST /api/messages/pre-inscription — c'est le lien que l'utilisateur clique pour confirmer lui-même sa demande sur WhatsApp (jamais d'envoi automatique depuis le serveur).
- Ajoute les champs telephone (obligatoire) et contacte (boolean) à NewsletterSubscription, supprime l'envoi de PDF par email.

VALIDATION TÉLÉPHONE CAMEROUNAIS :
Applique ce pattern sur TOUS les champs telephone (ContactCreateDto, PreInscriptionCreateDto, NewsletterSubscribeDto, etc.) :
@Pattern(regexp = "^(\\+?237)?6[5-9]\\d{7}$", message = "Numéro camerounais invalide")
Normalise le stockage en base au format 237XXXXXXXXX (sans espaces ni +) pour simplifier la génération des liens wa.me.

MIGRATIONS FLYWAY :
- Ajoute flyway-core et flyway-database-postgresql au pom
- spring.jpa.hibernate.ddl-auto=validate (jamais update en prod)
- Crée les migrations dans src/main/resources/db/migration/ : V1 (schéma initial complet basé sur les entités actuelles), V2 (ajout telephone + contacte sur newsletter_subscriptions), V3 (ajout colonne version sur formations pour le verrou optimiste), V4 (création table testimonials)

RATE LIMITING :
Ajoute bucket4j-core au pom. Crée un OncePerRequestFilter qui limite à 5 requêtes/10 minutes par IP sur /api/messages/contact, /api/messages/pre-inscription et /api/newsletter/subscribe. Retourne 429 avec un message JSON clair si dépassé.

VERROU OPTIMISTE SUR LES PLACES :
Ajoute @Version sur Formation.version. Dans FormationService.recordEnrollment(), ajoute @Retryable (spring-retry, 3 tentatives, backoff 100ms) sur ObjectOptimisticLockingFailureException pour gérer le cas de deux inscriptions simultanées sur la dernière place.

UPLOAD CLOUDINARY COMPLET :
Ajoute cloudinary-http5 au pom. spring.servlet.multipart.max-file-size=10MB. Crée CloudinaryConfig (bean Cloudinary depuis cloudinary.url) et UploadController avec POST /api/upload/image (upload avec quality:auto:good et fetch_format:auto pour compression/WebP automatique) et DELETE /api/upload/image/{publicId}.

AUTHENTIFICATION ADMIN :
Un seul compte admin via InMemoryUserDetailsManager, identifiants depuis variables d'environnement app.admin.username / app.admin.password. HTTP Basic activé (httpBasic()), stateless (pas de session serveur), CORS configuré avec l'origine du frontend en variable d'environnement.

ENTITÉ TESTIMONIAL :
Crée l'entité Testimonial (nomEtudiant, formationSuivie, temoignage, note 1-5, photoUrl, publie boolean, dateCreation) + repository + DTO + controller : GET /api/testimonials (public, uniquement publie=true), GET /api/testimonials/admin (tous), POST /api/testimonials (admin), PATCH /api/testimonials/{id}/publier (admin, toggle).

ARCHITECTURE DDD/HEXAGONALE (applique au minimum sur Formation et Message) :
domain/ (entités pures sans Spring/JPA, interfaces Port) → application/ (use cases, NotificationPort) → infrastructure/ (adapters JPA, config, seed) → presentation/ (controllers, DTOs, mappers MapStruct)

CONFIGURATION PROD (Render) :
Crée application-prod.properties utilisant uniquement des variables d'environnement : DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, WHATSAPP_ADMIN_NUMBER, ADMIN_USERNAME, ADMIN_PASSWORD, CLOUDINARY_URL, FRONTEND_URL (pour CORS). ddl-auto=validate, flyway activé, logs en INFO.

POM.XML FINAL :
Retire spring-boot-starter-mail, spring-boot-starter-thymeleaf, le driver mysql-connector-j (garde uniquement postgresql). Ajoute flyway-core, flyway-database-postgresql, bucket4j-core, spring-retry, cloudinary-http5, spring-boot-starter-actuator. Crée deux profils Maven dev/prod.

LIVRABLE ATTENDU, DANS CET ORDRE :
1. Nouvelle arborescence complète
2. pom.xml complet
3. Migrations Flyway (V1 à V4)
4. domain/ → application/ → infrastructure/ → presentation/
5. application.properties, application-dev.properties, application-prod.properties

[COLLE ICI TOUT LE CODE SOURCE BACKEND ACTUEL]
```

---

*v2 — intègre les retours de revue : confirmation utilisateur WhatsApp, validation téléphone, migrations, rate limiting, verrou optimiste, upload images, auth admin, déploiement Render/P


# IBE — Guide de Refactoring BACKEND
> Spring Boot 3.3 · Java 17 · DDD allégé + Architecture Hexagonale · WhatsApp-first
> Institut Beauty's Empire — Douala, Cameroun

---

## 1. Diagnostic du code actuel

### Ce qui est solide et à garder
- Entité `Formation` complète (prix FCFA, places, social proof, SEO fields)
- DTOs séparés par usage (Create / Update / List / Detail / Admin / Simple)
- `FormationUtils` bien pensé pour les calculs métier (taux remplissage, social proof, promo)
- Pagination + Specifications JPA pour la recherche

### Bugs critiques à corriger en premier
| # | Bug | Fichier | Impact |
|---|-----|---------|--------|
| 1 | `import java.nio.file.AccessDeniedException` au lieu de l'exception custom | `GlobalExceptionHandler.java` | Le handler `@ExceptionHandler(AccessDeniedException)` ne catch jamais la bonne exception, conflit silencieux avec Spring Security |
| 2 | Deux classes `FormationSimpleDto` (`dto/formation/` et `dto/contactInscription/`) | Les deux fichiers | Confusion MapStruct, imports ambigus, duplication de logique |
| 3 | `MessageMapper` utilise `uses = {FormationSimpleDto.class}` | `MessageMapper.java` | `uses` attend des **interfaces Mapper**, pas des DTOs → erreur de compilation ou mapping silencieusement ignoré |
| 4 | `FormationInitializer` revérifie nom par nom à chaque démarrage | `FormationInitializer.java` | Coûteux, fragile (un espace en trop = doublon) |
| 5 | Sécurité : tout `/api/**` potentiellement permissif | `SecurityConfig.java` | Les endpoints admin (POST/PUT/DELETE formations) ne sont pas protégés |
| 6 | Toute la stack email (EmailService, NotificationService avec retry, Thymeleaf) | `service/` | Inutile pour l'audience cible (WhatsApp-first), complexité et dépendances à retirer |
| 7 | `NewsletterSubscription` n'a pas de champ téléphone | `model/NewsletterSubscription.java` | Email seul ne sert à rien pour relancer un client au Cameroun |

---

## 2. Fonctionnalités attendues d'un institut de beauté en ligne (Afrique / Cameroun)

Liste basée sur ce qui fait vraiment fonctionner ce type de site dans le contexte camerounais/africain (pas du copier-coller US/Europe) :

**Indispensable**
- Catalogue formations avec prix en FCFA, durée, places restantes (social proof)
- Pré-inscription par formulaire → conversion finale sur WhatsApp (pas email)
- Numéro de téléphone obligatoire, email optionnel partout
- Bouton WhatsApp flottant permanent + lien pré-rempli par formation
- Galerie de réalisations (avant/après, travaux d'élèves)
- Page "agenda des sessions" (dates de prochaines rentrées)
- Mention certification (MINEFOP / DQP / CQP / AQP) — gage de confiance fort localement
- Dashboard admin pour gérer formations, voir les pré-inscriptions et contacts, et lister les abonnés "catalogue" à recontacter sur WhatsApp
- Site rapide sur connexion 3G/4G (images compressées, peu de JS bloquant)

**Très recommandé**
- Témoignages d'anciens élèves avec photo
- Packs/combos de formations (déjà présent dans ton modèle — bon signe)
- Carte Google Maps de localisation de l'institut
- Partage WhatsApp/Facebook optimisé (Open Graph correct)
- Statistiques affichées (nombre d'élèves formés, taux de satisfaction)

**Optionnel mais différenciant**
- Paiement Mobile Money (Orange Money / MTN MoMo) pour les frais d'inscription — à prévoir en V2, pas un bloqueur maintenant
- Multi-langue FR/EN si clientèle anglophone visée
- Blog SEO ("comment devenir prothésiste ongulaire au Cameroun")

---

## 3. Architecture cible — DDD allégé + Hexagonal

> Important : pour un projet de cette taille, on applique les **principes** DDD/hexagonal (séparation des responsabilités, domaine pur, ports/adapters) sans sur-ingénierie (pas de CQRS complet, pas d'event sourcing).

```
src/main/java/com/example/institue1/
│
├── domain/                              # Cœur métier — ZÉRO dépendance Spring/JPA
│   ├── formation/
│   │   ├── Formation.java               # Entité domaine pure (POJO)
│   │   ├── FormationRepositoryPort.java # Interface (port)
│   │   └── vo/
│   │       ├── Prix.java                # Value Object (prix + devise + calcul promo)
│   │       └── SocialProof.java         # Value Object (places, affichage, message)
│   ├── message/
│   │   ├── Message.java
│   │   ├── MessageRepositoryPort.java
│   │   ├── StatutMessage.java
│   │   └── TypeMessage.java
│   └── shared/
│       └── vo/Telephone.java            # Validation + formatage +237
│
├── application/                         # Cas d'usage — orchestre le domaine
│   ├── formation/
│   │   ├── FormationService.java        # Use cases (create/update/list/search)
│   │   └── dto/                         # Commands internes si besoin
│   ├── message/
│   │   └── MessageService.java
│   └── notification/
│       ├── NotificationPort.java        # Interface — pas de détail WhatsApp ici
│       └── WhatsAppLinkGenerator.java   # Génère les liens wa.me (pas d'envoi auto)
│
├── infrastructure/                      # Implémentations techniques
│   ├── persistence/
│   │   ├── jpa/
│   │   │   ├── FormationJpaEntity.java
│   │   │   ├── FormationJpaRepository.java
│   │   │   └── FormationRepositoryAdapter.java  # implémente FormationRepositoryPort
│   │   └── ...
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── WebMvcConfig.java
│   │   └── OpenApiConfig.java
│   └── seed/
│       └── FormationInitializer.java    # @Profile("!prod")
│
└── presentation/
    ├── api/
    │   ├── FormationController.java
    │   ├── MessageController.java
    │   ├── GalleryImageController.java
    │   └── NewsletterController.java
    ├── admin/
    │   └── AdminStatsController.java     # NOUVEAU — pour le dashboard
    ├── dto/                              # DTOs REST (Create/Update/List/Detail/Admin)
    └── mapper/                           # MapStruct — DTO ↔ Domain
```

**Règle d'or** : le `domain/` ne doit importer ni `jakarta.persistence.*`, ni `org.springframework.*`. Si tu dois ajouter une annotation JPA, c'est que ça appartient à `infrastructure/persistence/`.

> Pragmatisme : si refaire 100% de cette séparation prend trop de temps, applique-la au minimum sur `Formation` et `Message` (le cœur métier) — le reste (`GalleryImage`, `Newsletter`) peut rester en architecture en couches classique (`model/repository/service/controller`).

---

## 4. Corrections détaillées

### 4.1 GlobalExceptionHandler
```java
// SUPPRIMER
import java.nio.file.AccessDeniedException;

// REMPLACER PAR
import com.example.institue1.exception.AccessDeniedException;
```

### 4.2 FormationSimpleDto unifié
Garder **un seul** DTO dans `presentation/dto/formation/FormationSimpleDto.java`, fusionner les champs des deux versions :
```java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FormationSimpleDto {
    private Long id;
    private String nom;
    private String categorie;
    private BigDecimal prix;
    private String duree;
    private Boolean active;
    private String slug;
    private Integer placesRestantes; // fusionné depuis l'autre version
}
```
Supprimer `dto/contactInscription/FormationSimpleDto.java` et mettre à jour tous les imports (`MessageMapper`, `MessageDetailDto`, `ChangeStatutDto`...).

### 4.3 MessageMapper corrigé
```java
@Mapper(componentModel = "spring")
public interface FormationSimpleDtoMapper {
    FormationSimpleDto toSimpleDto(Formation formation);
}

@Mapper(
    componentModel = "spring",
    uses = {FormationSimpleDtoMapper.class},   // ← Mapper, pas DTO
    imports = {MessageUtils.class}
)
public interface MessageMapper { /* ... */ }
```

### 4.4 FormationInitializer
```java
@Component
@Profile("!prod")   // ← ne tourne jamais en production
public class FormationInitializer {
    @PostConstruct
    public void init() {
        if (formationRepository.count() == 0) {
            createFormations();
            logger.info("Seed initial effectué");
        } else {
            logger.info("Données existantes — seed ignoré");
        }
    }
}
```

### 4.5 Sécurité
```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers(HttpMethod.GET, "/api/formations/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/gallery/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/messages/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/newsletter/**").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/formations").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/api/formations/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/formations/**").hasRole("ADMIN")
    .requestMatchers("/api/formations/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/messages", HttpMethod.GET.name()).hasRole("ADMIN")
    .requestMatchers("/api/messages/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```
Pour le MVP, une sécurité simple **HTTP Basic + un seul compte admin en `application.properties`** suffit (pas besoin de JWT complexe tout de suite). JWT peut venir en V2.

---

## 5. WhatsApp-first — remplacer tout le système email

### À supprimer entièrement
- `service/EmailService.java`
- `templates/email/*.html` (Thymeleaf)
- Dépendances Maven : `spring-boot-starter-mail`, `spring-boot-starter-thymeleaf`
- Propriétés : `app.email.*`, `spring.mail.*`

### NotificationService simplifié (pas d'envoi auto — génère juste les liens)
```java
@Service
@Slf4j
public class NotificationService {

    @Value("${app.whatsapp.admin-number}")
    private String adminNumber;

    public String genererLienAdmin(Message message) {
        String texte = String.format(
            "🔔 Nouvelle %s IBE%n👤 %s%n📱 %s%n📚 %s%n🏙️ %s%n💬 %s",
            message.getType().getLibelle(),
            message.getNom(),
            message.getTelephone(),
            message.getFormationNom() != null ? message.getFormationNom() : "Contact général",
            message.getVille() != null ? message.getVille() : "Non renseigné",
            message.getMessage()
        );
        String num = normaliser(adminNumber);
        return "https://wa.me/" + num + "?text=" + URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }

    private String normaliser(String tel) {
        String clean = tel.replaceAll("[^0-9]", "");
        return clean.startsWith("237") ? clean : "237" + clean;
    }
}
```
Ce lien est stocké/affiché dans le dashboard admin (bouton "Contacter sur WhatsApp" à côté de chaque message). **Aucun envoi automatique** — c'est toujours un humain qui clique, donc zéro risque de bannissement.

### NewsletterSubscription + téléphone
```java
@Column(length = 20)
private String telephone; // obligatoire pour le catalogue WhatsApp
```
`NewsletterService.subscribeAndGetWhatsAppLink(telephone)` retourne un lien `wa.me` (l'utilisateur envoie lui-même le premier message — voir section 6).

---

## 6. Anti-spam WhatsApp — règle d'or

Ne **jamais** envoyer le premier message automatiquement vers un inconnu depuis le numéro de l'institut — c'est ce qui fait bannir un compte WhatsApp Business gratuit.

Flux correct : le bouton "Recevoir le catalogue" ouvre WhatsApp **côté visiteur** avec un message pré-rempli que LUI envoie. L'admin reçoit, répond avec le PDF. C'est l'utilisateur qui initie à chaque fois → 100% safe, gratuit, pas d'API payante nécessaire pour démarrer.

---

## 7. Endpoints Admin Dashboard à ajouter

```java
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @GetMapping("/overview")
    // → totalFormationsActives, messagesNonLus, preInscriptionsSemaine,
    //   abonnesNonContactes, messagesParJour (7 derniers jours)

    @GetMapping("/messages/urgents")
    // → liste des pré-inscriptions non traitées, triées par priorité

}

@RestController
@RequestMapping("/api/admin/newsletter")
public class AdminNewsletterController {

    @GetMapping
    // → liste abonnés (téléphone, date, contacté: bool)

    @PatchMapping("/{id}/marquer-contacte")
    // → toggle "contacté via WhatsApp"
}
```

---

## 8. pom.xml — nettoyage pour un projet "professionnel"

### À retirer
```xml
<!-- SUPPRIMER : email -->
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-mail</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-thymeleaf</artifactId></dependency>

<!-- SUPPRIMER si non utilisé réellement (vérifier d'abord) -->
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-webflux</artifactId></dependency>

<!-- GARDER UN SEUL driver DB en prod (actuellement MySQL + Postgres en même temps) -->
<!-- Choisir : soit mysql-connector-j, soit postgresql, pas les deux -->
```

### À ajouter
```xml
<!-- Validation des configs au démarrage -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>

<!-- Actuator — santé de l'app, utile en prod -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Pour upload images vers un stockage cloud (recommandé: Cloudinary, déjà mentionné dans ton code) -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http5</artifactId>
    <version>1.39.0</version>
</dependency>
```

### Build plugin — profils par environnement
```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <properties><spring.profiles.active>dev</spring.profiles.active></properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties><spring.profiles.active>prod</spring.profiles.active></properties>
    </profile>
</profiles>
```
Avec `application-dev.properties` (H2 ou MySQL local, logs DEBUG, seed activé) et `application-prod.properties` (variables d'environnement pour la DB, logs INFO, seed désactivé, CORS restreint au vrai domaine).

---

## 9. Checklist avant mise en production

- [ ] Un seul driver DB déclaré dans le pom (pas MySQL + Postgres)
- [ ] `app.whatsapp.admin-number` configuré avec le vrai numéro
- [ ] Identifiants DB en variables d'environnement, jamais en dur
- [ ] `FormationInitializer` désactivé en prod (`@Profile("!prod")`)
- [ ] CORS limité au vrai domaine du frontend (plus de `*` ni localhost)
- [ ] Endpoints admin protégés et testés (essayer un POST formation sans auth → doit échouer)
- [ ] Tous les tests des bugs 1 à 7 vérifiés manuellement

---

## 10. PROMPT À COPIER DANS AI STUDIO

```
Tu es un expert Spring Boot / Java 17 / architecture DDD-hexagonale. Je te fournis le code source complet d'un backend Spring Boot existant pour un institut de beauté au Cameroun (Institut Beauty's Empire). Je veux que tu le refactorises ENTIÈREMENT en respectant les règles ci-dessous. Ne me donne pas un résumé : donne-moi le code complet, fichier par fichier, prêt à coller.

CONTEXTE MÉTIER :
- Institut de formation beauté (onglerie, make-up, coiffure, esthétique, cosmétiques, packs combinés, DQP/AQP/CQP)
- Audience : utilisateurs camerounais, conversion principale via WhatsApp (PAS l'email)
- Le numéro de téléphone est le champ obligatoire partout, l'email devient optionnel
- Prix en FCFA (BigDecimal)
- Modèle "social proof" : nombre d'inscrits affiché peut différer du nombre réel (champ admin)

CORRECTIONS OBLIGATOIRES (bugs identifiés) :
1. GlobalExceptionHandler importe `java.nio.file.AccessDeniedException` au lieu de l'exception custom du projet `com.example.institue1.exception.AccessDeniedException` — corrige cet import.
2. Il existe DEUX classes FormationSimpleDto dans des packages différents (dto/formation/ et dto/contactInscription/) qui entrent en conflit avec MapStruct. Fusionne-les en UNE seule classe enrichie (avec le champ placesRestantes) et mets à jour tous les imports.
3. MessageMapper utilise `uses = {FormationSimpleDto.class}` dans son annotation @Mapper — c'est invalide, `uses` attend des interfaces Mapper. Crée un FormationSimpleDtoMapper dédié et utilise-le.
4. FormationInitializer recrée des formations à chaque redémarrage en testant nom par nom — remplace par une vérification `if (repository.count() == 0)` et ajoute `@Profile("!prod")`.
5. La sécurité actuelle expose trop de endpoints en public — sécurise tous les endpoints d'écriture (POST/PUT/DELETE sur /api/formations, tout /api/admin/**) derrière ROLE_ADMIN avec Spring Security (HTTP Basic suffit pour le MVP), garde les GET publics sur /api/formations et /api/gallery, et les POST publics sur /api/messages et /api/newsletter.

CHANGEMENT MAJEUR — SUPPRIMER LE SYSTÈME EMAIL :
- Supprime EmailService.java, tous les templates Thymeleaf email/, les dépendances Maven spring-boot-starter-mail et spring-boot-starter-thymeleaf, toutes les properties spring.mail.* et app.email.*
- Remplace NotificationService par une version qui génère uniquement des LIENS WhatsApp (https://wa.me/{numero}?text={message encodé}) — AUCUN envoi automatique, car un compte WhatsApp gratuit qui envoie en masse se fait bannir. Le lien est destiné à être affiché dans un dashboard admin pour qu'un humain clique dessus.
- Ajoute un champ `telephone` (String) à NewsletterSubscription, et change NewsletterService pour qu'il retourne un lien WhatsApp au lieu d'envoyer un PDF par email.

ARCHITECTURE CIBLE — DDD allégé + Hexagonal :
Réorganise les packages selon cette structure (applique-la au minimum sur Formation et Message, le cœur métier) :
- domain/ : entités métier pures, zéro annotation Spring/JPA, interfaces "Port" pour les repositories
- application/ : services qui orchestrent les cas d'usage (FormationService, MessageService), interfaces NotificationPort
- infrastructure/ : implémentations JPA (adapters), config Spring Security, seed
- presentation/ : controllers REST, DTOs, mappers MapStruct

Garde Lombok et MapStruct. Garde la pagination et les Specifications JPA existantes pour la recherche.

NOUVEAUTÉ — Dashboard Admin (endpoints à créer) :
- GET /api/admin/stats/overview → { totalFormationsActives, messagesNonLus, preInscriptionsSemaine, abonnesNonContactes }
- GET /api/admin/stats/messages/urgents → liste des messages PRE_INSCRIPTION non traités triés par priorité
- GET /api/admin/newsletter → liste paginée des abonnés avec leur statut "contacté" (booléen)
- PATCH /api/admin/newsletter/{id}/marquer-contacte

POM.XML — nettoyage :
- Retire spring-boot-starter-mail, spring-boot-starter-thymeleaf
- Garde UN SEUL driver DB (demande-moi lequel si ambigu, ne mets pas mysql-connector-j ET postgresql en même temps)
- Ajoute spring-boot-starter-actuator
- Crée deux profils Maven (dev / prod) avec application-dev.properties et application-prod.properties séparés (la prod doit utiliser des variables d'environnement pour la datasource, désactiver le seed, restreindre CORS)

CONTRAINTES DE STYLE :
- Java 17, Spring Boot 3.3, Lombok partout où c'est utile (@Data, @Builder, @Slf4j)
- Validation Jakarta (@NotBlank, @Pattern pour téléphone format +237)
- Garde le pattern ApiResponse<T> existant pour toutes les réponses REST
- Commente en français les choix d'architecture non évidents

LIVRABLE ATTENDU :
Donne-moi la nouvelle arborescence complète des packages, puis le code complet de chaque fichier nouveau ou modifié, dans l'ordre : domain/ → application/ → infrastructure/ → presentation/ → pom.xml → application-dev.properties → application-prod.properties.

[COLLE ICI TOUT LE CODE SOURCE BACKEND ACTUEL : entités, DTOs, controllers, services, repositories, mappers, utils, exceptions, enums, config, FormationInitializer, pom.xml]
```

---

*Fichier généré pour servir de base à un refactoring assisté par IA — vérifie toujours le code généré avant de le committer.*ostgreSQL.*