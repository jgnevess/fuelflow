package br.com.joaonevesdev.fuelflow.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FuelFlow API",
                version = "v1",
                description = """
                        API construída a partir de dados públicos abertos (ANP).
                        Não oficial. Sem vínculo com órgãos governamentais.
                        A fonte original deve ser considerada para validação final dos dados.
        """
        )
)
public class OpenApiConfig {
}
