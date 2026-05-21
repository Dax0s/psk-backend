package org.kotletai.backend.aspect

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.time.Instant

private val log = KotlinLogging.logger {}

@Aspect
@Component
@EnableAspectJAutoProxy
class AuditLogAspect(
    private val properties: AuditProperties,
) {
    @Pointcut("execution(* org.kotletai.backend.service..*(..))")
    fun servicePointcut() {}

    @Pointcut("execution(* org.kotletai.backend.scheduler..*(..))")
    fun schedulerPointcut() {}

    @Around("servicePointcut() || schedulerPointcut()")
    fun audit(joinPoint: ProceedingJoinPoint): Any? {
        if (!properties.enabled) return joinPoint.proceed()

        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal
        val username =
            when (principal) {
                is Jwt ->
                    principal.getClaimAsString("preferred_username")
                        ?: principal.getClaimAsString("cognito:username")
                        ?: principal.subject
                else -> auth?.name ?: "system"
            }
        val authorities = auth?.authorities?.joinToString(",") { it.authority ?: "" } ?: ""
        val className = joinPoint.signature.declaringType.simpleName
        val methodName = joinPoint.signature.name
        val timestamp = Instant.now()
        val args =
            if (properties.includeArgs) {
                joinPoint.args.joinToString(",") { arg: Any? -> arg?.toString() ?: "null" }
            } else {
                "<redacted>"
            }

        val start = System.nanoTime()
        return try {
            val result = joinPoint.proceed()
            val elapsedMs = (System.nanoTime() - start) / 1_000_000
            log.info {
                "AUDIT at=$timestamp user=$username authorities=[$authorities] " +
                    "method=$className.$methodName elapsedMs=$elapsedMs args=$args outcome=OK"
            }
            result
        } catch (e: Throwable) {
            val elapsedMs = (System.nanoTime() - start) / 1_000_000
            log.warn {
                "AUDIT at=$timestamp user=$username authorities=[$authorities] " +
                    "method=$className.$methodName elapsedMs=$elapsedMs args=$args " +
                    "outcome=ERROR(${e.javaClass.simpleName}: ${e.message})"
            }
            throw e
        }
    }
}
