package xh.destiny.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("xh.destiny.processor.Greet")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class GreetProcessor extends AbstractProcessor {

//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> types = new LinkedHashSet<>();
//        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
//            types.add(annotation.getCanonicalName());
//        }
//        return super.getSupportedAnnotationTypes();
//    }
//
//    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
//        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
//
//        annotations.add(Greet.class);
//        return annotations;
//    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Greet.class);
        List<TypeElement> types = ElementFilter.typesIn(annotatedElements);
        String packageName = null;
        String[] names = null;

        for (TypeElement type : types) {
            PackageElement packageElement = (PackageElement) type.getEnclosingElement();
            packageName = packageElement.getQualifiedName().toString();
            names = type.getAnnotation(Greet.class).value();
        }

        if (packageName == null) return false;

        /*StringBuilder builder = new StringBuilder()
                .append("package " + packageName + ";\n\n")
                .append("public class Greeter {\n\n")
                .append("   public static String hello() {\n")
                .append("       return \"Hello ");

        for (int i = 0; i < names.length; i++) {
            if (i == 0) {
                builder.append(names[i]);
            } else {
                builder.append(", ").append(names[i]);
            }
        }

        builder.append("!\";\n")
                .append("   }\n")
                .append("}\n");*/

        // 2.构造java代码
        MethodSpec hello = MethodSpec.methodBuilder("hello")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addStatement("return $S", "hello " + getFormattedNames(names) + "!")
                .build();

        TypeSpec greeter = TypeSpec.classBuilder("Greeter")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(hello)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, greeter).build();

        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(packageName + ".Greeter");
            Writer writer = javaFileObject.openWriter();
            writer.write(javaFile.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    private String getFormattedNames(String[] names) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            if (i == 0) {
                builder.append(names[i]);
            } else {
                builder.append(", ").append(names[i]);
            }
        }
        return builder.toString();
    }
}
