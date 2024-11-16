package top.sacz.timtool.scanner

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
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

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

    override fun process(resolver: Resolver): List<KSAnnotated> {
        //获取被注解标记的类列表
        val symbols =
            resolver.getSymbolsWithAnnotation("top.sacz.timtool.hook.core.annotation.HookItem")
            .filterIsInstance<KSClassDeclaration>()
            .toList()
        if (symbols.isEmpty()) return emptyList()
        //返回类型
        val returnType = ClassName("kotlin", "Array")
        //基😮类
        val genericsType = ClassName("top.sacz.timtool.hook.base", "BaseHookItem")
        //方法构建
        val methodBuilder = FunSpec.builder("getAllHookItems")
        methodBuilder.returns(returnType.parameterizedBy(genericsType))//泛型返回
        methodBuilder.addCode(
            CodeBlock.Builder().apply {
                add("return arrayOf(")
                for (symbol in symbols) {
                    val typeName = symbol.toClassName()
                    add("%T(),", typeName)
                }
                add(")")
            }.build()
        )

        val dependencies = Dependencies(true, *Array(symbols.size) {
            symbols[it].containingFile!!
        })

        FileSpec.builder("top.sacz.timtool.hook.gen", "HookItemEntryList")
            .addFunction(methodBuilder.build())
            .build()
            .writeTo(codeGenerator, dependencies)

        return emptyList()
    }
}
