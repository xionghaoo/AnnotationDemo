package xh.destiny.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("xh.destiny.processor.RetrofitPostRequest")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class RetrofitPostRequestProcessor extends AbstractProcessor {
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(RetrofitPostRequest.class);
        List<ExecutableElement> methods = ElementFilter.methodsIn(annotatedElements);

        if (annotatedElements.size() == 0) return false;

        String methodName = null;
        String value = null;

        note("annotatedElements: " + annotatedElements);

        for (ExecutableElement method : methods) {
//            PackageElement packageElement = (PackageElement) type.getEnclosingElement();
//            packageName = packageElement.getQualifiedName().toString();
//            names = type.getAnnotation(Greet.class).value();
            methodName = method.getSimpleName().toString();

            List<? extends VariableElement> elements = method.getParameters();
            value = method.getAnnotation(RetrofitPostRequest.class).value();

            note("RetrofitPostRequestProcessor method: " + method.getDefaultValue());
            note("RetrofitPostRequestProcessor value: " + value);
            note("RetrofitPostRequestProcessor elements: " + elements.size());
        }

//        MethodSpec hello = MethodSpec.methodBuilder("hello")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(String.class)
//                .addStatement("return $S", "hello " + getFormattedNames(names) + "!")
//                .build();

        TypeSpec greeter = TypeSpec.classBuilder("A" + methodName)
                .addModifiers(Modifier.PUBLIC)
//                .addMethod(hello)
                .build();

        JavaFile javaFile = JavaFile.builder("xh.destiny.processor", greeter).build();
//
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler()
                    .createSourceFile( "xh.destiny.processor.A" + methodName);
            Writer writer = javaFileObject.openWriter();
            writer.write(javaFile.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (packageName == null) return false;

        return false;
    }

    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void note(String format, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }
}
