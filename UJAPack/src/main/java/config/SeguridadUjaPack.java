package config;

import beans.ServicioRestAPI;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SeguridadUjaPack extends WebSecurityConfigurerAdapter {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        httpsec.httpBasic();

        String path = ServicioRestAPI.URI_MAPPING;

        httpsec.authorizeRequests().antMatchers(path+"/**").hasRole("ADMIN");
        httpsec.authorizeRequests().antMatchers(HttpMethod.GET, path+"/envios/{id}").access("hasRole('USER') and #id !='extraviados' ");
        httpsec.authorizeRequests().antMatchers(HttpMethod.GET, path+"/envios/{id}/*").access("hasRole('USER') and #id !='extraviados' ");
        httpsec.authorizeRequests().antMatchers(HttpMethod.PUT, path+"/envios/{id}/{noti}").access("hasRole('USER') and #noti =='nuevanotificacion' ");
        httpsec.authorizeRequests().antMatchers(HttpMethod.POST, path+"/envios/**").hasRole("OPERATOR");
        httpsec.authorizeRequests().antMatchers(HttpMethod.GET, path+"/envios/**").hasRole("OPERATOR");
        httpsec.authorizeRequests().antMatchers(HttpMethod.PUT, path+"/envios/*").hasRole("TRANSPORT");




    }
}
