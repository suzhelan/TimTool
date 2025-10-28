package top.sacz.timtool.scanner

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import top.sacz.timtool.hook.core.annotation.HookItem

/**
 * ksp根据注解动态生成代码
 */
class HookItemProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return HookItemScanner(environment.codeGenerator, environment.logger)
    }
}

class HookItemScanner(
    private val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {

    companion object {
        /**
         * 不动
         */
        private const val RETURN_TYPE_PACKAGE = "kotlin.collections"

        /**
         * 不动
         */
        private const val RETURN_TYPE_NAME = "List"

        /**
         * Base类包名
         */
        private const val BASE_CLASS_PACKAGE = "top.sacz.timtool.hook.base"

        /**
         * Base类名
         */
        private const val BASE_CLASS_NAME = "BaseHookItem"

        /**
         * 生成的包名
         */
        private const val GENERATED_PACKAGE = "top.sacz.timtool.hook.gen"

        /**
         * 要扫描的字段
         */
        private const val TARGET_ANNOTATION = "top.sacz.timtool.hook.core.annotation.HookItem"

        /**
         * 生成的类名称
         * */
        private const val GENERATED_CLASS_NAME = "HookItemEntryList"

        /**
         * 生成的方法名称
         */
        private const val GENERATED_FUNCTION_NAME = "getAllHookItems"
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        //获取被注解标记的类列表
        val symbols =
            resolver.getSymbolsWithAnnotation(TARGET_ANNOTATION)
                .filterIsInstance<KSClassDeclaration>()
                .toList()
        if (symbols.isEmpty()) return emptyList()
        //返回类型
        val returnType = ClassName(RETURN_TYPE_PACKAGE, RETURN_TYPE_NAME)
        //基类
        val genericsType = ClassName(BASE_CLASS_PACKAGE, BASE_CLASS_NAME)
        //方法构建
        val methodBuilder = FunSpec.builder(GENERATED_FUNCTION_NAME)
        methodBuilder.returns(returnType.parameterizedBy(genericsType))//泛型返回
        methodBuilder.addAnnotation(JvmStatic::class)//jvm静态方法
        methodBuilder.addCode(
            CodeBlock.Builder().apply {
                addStatement("val list = mutableListOf<%T>()", genericsType)
                for (symbol in symbols) {
                    val typeName = symbol.toClassName()
                    //获取 hook item 注解
                    val hookItem = symbol.getAnnotationsByType(HookItem::class).first()
                    //提取注解内容
                    val itemName = hookItem.value
                    //获取类名称（简单）
                    val valName = symbol.toClassName().simpleName
                    //构建对象并且设置item name
                    addStatement("val %N = %T()", valName, typeName)
                    addStatement("%N.setPath(%S)", valName, itemName)
                    addStatement("list.add(%N)", valName)
                }
                addStatement("return %N", "list")
            }.build()
        )

        //class
        val classSpec = TypeSpec.objectBuilder(GENERATED_CLASS_NAME)
            .addFunction(methodBuilder.build())
            .build()
        val dependencies = Dependencies(true, *Array(symbols.size) {
            symbols[it].containingFile!!
        })
        //文件
        FileSpec.builder(GENERATED_PACKAGE, GENERATED_CLASS_NAME)
            .addType(classSpec)
            .build()
            .writeTo(codeGenerator, dependencies)

        return emptyList()
    }
}
