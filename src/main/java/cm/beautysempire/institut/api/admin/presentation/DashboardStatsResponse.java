package cm.beautysempire.institut.api.admin.presentation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long messagesNonLus;
    private long formationsActives;
    private long abonnesNewsletter;
    private long totalTemoignages;
}