package com.example.api.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class LocaleConfig {

	Locale locale = new Locale("pt", "BR");

	@Bean
	Locale getDefaultLocale() {
		return locale;
	}

	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(locale);
		return slr;
	}

}
