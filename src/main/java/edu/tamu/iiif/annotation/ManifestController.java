package edu.tamu.iiif.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@ConditionalOnExpression
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface ManifestController {

    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] path();

    @AliasFor(annotation = ConditionalOnExpression.class, attribute = "value")
    String condition();

}
