package com.task.management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Task API",
                version = "0.0.1",
                contact = @Contact(
                        name = "Viktor",
                        email = "vitekman@mail.ru"
                )))
public class OpenApiConfig {

}