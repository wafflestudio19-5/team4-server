package wafflestudio.team4.reddit.global.util.auth

import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotationUtils

object AnnotationUtil {
    fun <T> findMethodAnnotation(annotationClass: Class<T>, parameter: MethodParameter): T? where T : Annotation {
        val annotation = parameter.getMethodAnnotation(annotationClass)
        if (annotation != null) {
            return annotation
        }
        val annotationsToSearch: Array<Annotation> = parameter.parameterAnnotations
        annotationsToSearch.forEach {
            toSearch ->
            val foundAnnotation = AnnotationUtils.findAnnotation(toSearch.annotationClass::class.java, annotationClass)
            if (foundAnnotation != null) {
                return foundAnnotation
            }
        }
        return null
    }
}
