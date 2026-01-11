package org.example.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ArtistServiceClient {

    @Autowired
    @Qualifier("directRestTemplate")
    private RestTemplate restTemplate;

    @Value("${services.artist-service.url:http://ARTIST-SERVICE}")
    private String artistServiceUrl;

    public ArtistDTO getArtistById(Long id) {
        String url = artistServiceUrl + "/api/artists/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Requested-With", "Gateway-Service");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<ArtistDTO> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            ArtistDTO.class
        );
        
        return response.getBody();
    }

    public List<ArtistDTO> getAllArtists() {
        String url = artistServiceUrl + "/api/artists";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Requested-With", "Gateway-Service");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<List<ArtistDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<ArtistDTO>>() {}
        );
        
        return response.getBody();
    }

    public static class ArtistDTO {
        private Long id;
        private String name;
        private String genre;
        private Integer age;
        private String nationality;
        private String email;
        private String biography;
        private Double rating;
        private Boolean isActive;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getNationality() { return nationality; }
        public void setNationality(String nationality) { this.nationality = nationality; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getBiography() { return biography; }
        public void setBiography(String biography) { this.biography = biography; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}

