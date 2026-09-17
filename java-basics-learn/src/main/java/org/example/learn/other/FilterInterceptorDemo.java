package org.example.learn.other;

import java.util.List;
import java.util.function.Consumer;

/**
 * Filter 包住整条链；Interceptor 只包 Controller。对应 Spring MVC 拦截器 vs 过滤器。
 */
public final class FilterInterceptorDemo {

    public static void run() {
        List<Filter> filters = List.of((req, chain) -> {
            System.out.println("[Filter] 鉴权/包装 request 前");
            chain.accept(req);
            System.out.println("[Filter] 后");
        });
        List<Interceptor> interceptors = List.of(new Interceptor() {
            @Override
            public boolean preHandle(String req) {
                System.out.println("[Interceptor] preHandle 能拿到 Handler");
                return true;
            }

            @Override
            public void afterCompletion(String req) {
                System.out.println("[Interceptor] afterCompletion");
            }
        });

        String req = "GET /users/1";
        runChain(filters, 0, req, r -> {
            for (Interceptor it : interceptors) {
                if (!it.preHandle(r)) {
                    return;
                }
            }
            System.out.println("[Controller] " + r);
            for (int i = interceptors.size() - 1; i >= 0; i--) {
                interceptors.get(i).afterCompletion(r);
            }
        });
        System.out.println("  Filter：Servlet 规范，无 Spring 也能跑。Interceptor：进 DispatcherServlet 之后，能注入 Bean。");
    }

    private static void runChain(List<Filter> filters, int idx, String req, Consumer<String> last) {
        if (idx < filters.size()) {
            filters.get(idx).doFilter(req, r -> runChain(filters, idx + 1, r, last));
        } else {
            last.accept(req);
        }
    }

    interface Filter {
        void doFilter(String req, Consumer<String> chain);
    }

    interface Interceptor {
        boolean preHandle(String req);

        void afterCompletion(String req);
    }
}
