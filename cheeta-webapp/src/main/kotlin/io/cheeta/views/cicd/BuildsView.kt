package io.cheeta.views.cicd

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.router.Route
import io.cheeta.MainLayout
import io.cheeta.api.client.CheetaApiClient
import io.cheeta.api.dto.BuildDto
import org.springframework.beans.factory.annotation.Autowired

@Route("builds", layout = MainLayout::class)
class BuildsView(
    @Autowired private val api: CheetaApiClient
) : Div() {
    
    init {
        setSizeFull()
        style.set("padding", "20px")
        
        add(
            H1("CI/CD Builds").apply {
                style.set("color", "#F59E0B")
            },
            createBuildsGrid()
        )
    }

    private fun createBuildsGrid(): Grid<BuildDto> {
        return Grid<BuildDto>().apply {
            addColumn { it.number?.toString() ?: "-" }.setHeader("#").setWidth("80px")
            addColumn { it.branch }.setHeader("Branch").setFlexGrow(1)
            addColumn { it.status }.setHeader("Status").setWidth("100px")
            addColumn { it.author?.name ?: "-" }.setHeader("Author").setFlexGrow(1)
            
            setItems(emptyList())
            setSizeFull()
        }
    }
}
