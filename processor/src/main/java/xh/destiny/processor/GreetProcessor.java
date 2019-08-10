package xh.destiny.processor;

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
        // 1.获取类型信息
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

        // 2.构造Java代码
        StringBuilder builder = new StringBuilder()
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
                .append("}\n");

        // 3.生成Java源文件
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(packageName + ".Greeter");
            Writer writer = javaFileObject.openWriter();
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
