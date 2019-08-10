package xh.destiny.processor;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("xh.destiny.processor.Greet")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GreetProcessor extends AbstractProcessor {
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

        // 2. write the greeter class
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

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, "./processor/src/main/resources/template");
        velocityEngine.init();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("packageName", packageName);
        velocityContext.put("className", "Greeter");
        velocityContext.put("methodName", "hello");
        velocityContext.put("names", getFormattedNames(names));

        Template template = velocityEngine.getTemplate("greeter.vm");

        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(packageName + ".Greeter");
            Writer writer = javaFileObject.openWriter();
//            writer.write(builder.toString());
            template.merge(velocityContext, writer);
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
