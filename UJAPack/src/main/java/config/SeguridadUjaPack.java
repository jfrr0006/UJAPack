package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import rest.RestEnvio;

@Configuration
public class SeguridadUjaPack extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
                .withUser("user").roles("USER","DEFAULT").password("{noop}password");

    }


    @Override
    protected void configure(HttpSecurity httpsec) throws Exception{
        httpsec.csrf().disable();
        httpsec.httpBasic();

        //Para RESTEnvio
        String path = RestEnvio.URI_MAPPING;
        httpsec.authorizeRequests().antMatchers(HttpMethod.POST, path).permitAll();//
    }
}
