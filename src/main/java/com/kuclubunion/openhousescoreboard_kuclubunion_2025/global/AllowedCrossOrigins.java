package com.kuclubunion.openhousescoreboard_kuclubunion_2025.global;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "web.cors")
@Data
@NoArgsConstructor
public class AllowedCrossOrigins {
  public List<String> origins;
}
