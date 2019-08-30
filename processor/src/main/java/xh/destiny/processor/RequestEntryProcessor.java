package xh.destiny.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("xh.destiny.processor.RequestEntry")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class RequestEntryProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(RequestEntry.class);
        List<ExecutableElement> methods = ElementFilter.methodsIn(annotatedElements);

        if (annotatedElements.size() == 0) return false;

        note("annotatedElements: " + annotatedElements);

        // 遍历所有的注解方法
        for (ExecutableElement method : methods) {
            // 获取方法所在的包名
            String packageName = processingEnv.getElementUtils().getPackageOf(method).toString();

            String methodName = method.getSimpleName().toString();

            List<? extends VariableElement> parameters = method.getParameters();

            // 遍历方法的参数
            for (VariableElement parameter : parameters) {
                note("parameter: " + parameter.getSimpleName());
            }

            String generateClassName = "_" + method.getAnnotation(RequestEntry.class).value() + "Entry";

            note("packageName: " + packageName);

            TypeSpec.Builder builder = TypeSpec.classBuilder(generateClassName)
                    .addModifiers(Modifier.PUBLIC);
//            for (VariableElement parameter : parameters) {
//                note("parameter: " + parameter.getSimpleName());
//            }

            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);

            for (VariableElement parameter : parameters) {
                String fieldName = parameter.getSimpleName().toString();
                FieldSpec field = FieldSpec.builder(TypeName.get(parameter.asType()),
                        fieldName, Modifier.PUBLIC, Modifier.FINAL).build();
                builder.addField(field);

                methodBuilder.addParameter(TypeName.get(parameter.asType()), fieldName)
                        .addStatement("this.$N = $N", fieldName, fieldName);
            }

            builder.addMethod(methodBuilder.build());

            TypeSpec type = builder.build();

            JavaFile javaFile = JavaFile.builder(packageName, type).build();

            try {
                JavaFileObject javaFileObject = processingEnv.getFiler()
                        .createSourceFile( packageName + "." + generateClassName);
                Writer writer = javaFileObject.openWriter();
                writer.write(javaFile.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void note(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void note(String format, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }
}
