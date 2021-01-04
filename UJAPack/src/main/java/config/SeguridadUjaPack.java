package config;

import beans.ServicioRestAPI;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SeguridadUjaPack extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").roles("USER").password("{noop}user")
                .and()
                .withUser("operator").roles("OPERATOR").password("{noop}operator")
                .and()
                .withUser("transport").roles("TRANSPORT").password("{noop}transport")
                .and()
                .withUser("admin").roles("ADMIN").password("{noop}admin");

    }


    @Override
    protected void configure(HttpSecurity httpsec) throws Exception {
        httpsec.csrf().disable();
        httpsec.cors().and().httpBasic();

        String path = ServicioRestAPI.URI_MAPPING;

        httpsec.authorizeRequests().antMatchers(path + "/envios/public/**").permitAll();
        httpsec.authorizeRequests().antMatchers(HttpMethod.GET,path + "/envios/private/**").hasAnyRole("ADMIN", "OPERATOR");
        httpsec.authorizeRequests().antMatchers(HttpMethod.PUT, path + "/envios/private/**").hasAnyRole("ADMIN", "TRANSPORT");
        httpsec.authorizeRequests().antMatchers(HttpMethod.POST, path + "/envios/private/**").hasAnyRole("ADMIN", "OPERATOR");


    }
}
