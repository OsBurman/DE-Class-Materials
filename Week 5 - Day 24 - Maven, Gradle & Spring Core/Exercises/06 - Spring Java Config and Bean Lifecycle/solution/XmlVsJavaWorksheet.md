# XML vs Java Configuration — Solution

## Part 3 Questions

**Q1.** What is the XML equivalent of `@Configuration`?

> The XML file itself acts as the configuration source — there is no direct equivalent annotation. The `<beans>` root element in a `beans.xml` file is what you register with `ClassPathXmlApplicationContext("beans.xml")`, just as you pass a `@Configuration` class to `AnnotationConfigApplicationContext`. If you want an analogy, the `<beans>` root element maps to `@Configuration`.

**Q2.** What is the XML equivalent of `@Bean`?

> The `<bean>` element. For example:
> ```xml
> <bean id="catalogService" class="com.library.CatalogService"
>       init-method="onStartup" destroy-method="onShutdown">
>     <constructor-arg ref="notificationService"/>
> </bean>
> ```
> This is the XML equivalent of:
> ```java
> @Bean(initMethod = "onStartup", destroyMethod = "onShutdown")
> public CatalogService catalogService() {
>     return new CatalogService(notificationService());
> }
> ```

**Q3.** Which approach is preferred today and why?

> **Java-based configuration** (`@Configuration`) is the modern standard for three main reasons:
> 1. **Type safety** — the IDE and compiler catch errors in Java config at compile time; XML typos are only caught at runtime.
> 2. **Refactoring support** — renaming a class in Java config is handled automatically by IDE refactoring tools; XML string references (`class="com.library.CatalogService"`) will silently break.
> 3. **Readability** — Java config reads like code, not markup. Complex conditional bean registration is trivial in Java (`if/else`, environment checks) but verbose and error-prone in XML profiles.
>
> XML config is still encountered in legacy enterprise applications and is worth being able to read, but all new Spring Boot projects use Java (or annotation-based) configuration.
