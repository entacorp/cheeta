package io.cheeta.views

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.router.Route
import io.cheeta.MainLayout

@Route("", layout = MainLayout::class)
class HomeView : Div() {
    init {
        setSizeFull()
        style.set("padding", "20px")
        
        add(
            H1("🐆 Welcome to Cheeta").apply {
                style.set("color", "#F59E0B")
                style.set("margin-bottom", "20px")
            },
            Paragraph(
                """
                The AI-powered, social DevOps platform built by developers, for developers.
                """.trimIndent()
            ).apply {
                style.set("font-size", "16px")
                style.set("max-width", "600px")
            },
            Div().apply {
                style.set("margin-top", "30px")
                style.set("padding", "20px")
                style.set("background-color", "#F3F4F6")
                style.set("border-radius", "8px")
                add(
                    Paragraph("Getting Started:").apply {
                        style.set("font-weight", "bold")
                        style.set("margin-top", "0px")
                    },
                    Paragraph("Browse repositories, track issues, and manage CI/CD pipelines all in one place.").apply {
                        style.set("margin", "10px 0")
                    }
                )
            }
        )
    }
}
