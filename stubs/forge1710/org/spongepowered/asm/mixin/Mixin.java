package org.spongepowered.asm.mixin;
import java.lang.annotation.*;
@Target(ElementType.TYPE) @Retention(RetentionPolicy.CLASS)
public @interface Mixin {
	Class<?>[] value() default {};
	String[] targets() default {};
	boolean remap() default true;
}
