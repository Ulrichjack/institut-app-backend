package cm.beautysempire.institut.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CloudinaryStorageAdapterTest {
    @Test
    void transmetLeFichierOriginalSansCompressionNiRedimensionnementALUpload() throws Exception {
        Cloudinary cloudinary = mock(Cloudinary.class);
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of(
                "secure_url", "https://example.invalid/original.jpg", "public_id", "original"));
        byte[] original = {1, 2, 3, 4};
        MockMultipartFile file = new MockMultipartFile("file", "original.jpg", "image/jpeg", original);

        String[] result = new CloudinaryStorageAdapter(cloudinary).uploadImage(file, "ibe/galerie");

        ArgumentCaptor<Map> options = ArgumentCaptor.forClass(Map.class);
        verify(uploader).upload(eq(original), options.capture());
        assertThat(options.getValue()).containsOnlyKeys("folder");
        assertThat(options.getValue().get("folder")).isEqualTo("ibe/galerie");
        assertThat(result).containsExactly("https://example.invalid/original.jpg", "original");
    }
}
