package uz.kundalik.site.config.logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

//    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {

        System.out.println("\n\033[0;32m🔔  📥  NEW REQUEST RECEIVED\033[0m");
        System.out.println("➡️  Method : " + request.getMethod());
        System.out.println("🌐  URI    : " + request.getRequestURI());
        System.out.println("🔍  Query  : " + (request.getQueryString() != null ? request.getQueryString() : "—"));
        System.out.println("🛡️  Token  : " + (request.getHeader("Authorization") != null ? request.getHeader("Authorization") : "—"));
        System.out.println("⏱️  Time   : " + new java.util.Date());
        System.out.println("✅  Proceeding to handler...\n");
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if (ex != null) {
            System.out.println("\n\033[0;31m⚠️  EXCEPTION ENCOUNTERED:\033[0m");
            System.out.println("➡️  Exception: " + ex.getMessage());
            System.out.println("🌐  URI: " + request.getRequestURI());
            System.out.println("⏱️  Time: " + new java.util.Date());
            System.out.println("❗  Status: " + response.getStatus());
            System.out.println("💥  Error occurred during request handling\n");
        }

        else if (response.getStatus() >= 400) {
            System.out.println("\n\033[0;31m⚠️  ERROR RESPONSE RECEIVED:\033[0m");
            System.out.println("\033[0;31m➡️  Status Code: " + response.getStatus() + "\033[0m");
            System.out.println("\033[0;31m🌐  URI: " + request.getRequestURI() + "\033[0m");
            System.out.println("\033[0;31m⏱️  Time: " + new java.util.Date() + "\033[0m");
            System.out.println("\033[0;31m💥  Error occurred during request handling \033[0m");
        }

        else {
            System.out.println("\n\033[0;32m✅  SUCCESSFUL REQUEST:\033[0m");
            System.out.println("\033[0;32m➡️  Status Code: " + response.getStatus() + "\033[0m");
            System.out.println("\033[0;32m🌐  URI: " + request.getRequestURI() + "\033[0m");
            System.out.println("\033[0;32m⏱️  Time: " + new java.util.Date() + "\033[0m");
            System.out.println("\033[0;32m👍  Request successfully processed \033[0m");
        }
    }

}
