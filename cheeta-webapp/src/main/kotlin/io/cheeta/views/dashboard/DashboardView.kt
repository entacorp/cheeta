package io.cheeta.views.dashboard

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.router.Route
import io.cheeta.MainLayout
import io.cheeta.api.client.CheetaApiClient
import io.cheeta.api.dto.ProjectDto
import org.springframework.beans.factory.annotation.Autowired

@Route("dashboard", layout = MainLayout::class)
class DashboardView(
    @Autowired private val api: CheetaApiClient
) : Div() {
    
    init {
        setSizeFull()
        style.set("padding", "20px")
        
        add(
            H1("Dashboard").apply {
                style.set("color", "#F59E0B")
            },
            createProjectsGrid()
        )
    }

    private fun createProjectsGrid(): Grid<ProjectDto> {
        return Grid<ProjectDto>().apply {
            addColumn { it.name }.setHeader("Project Name").setFlexGrow(2)
            addColumn { it.description }.setHeader("Description").setFlexGrow(3)
            addColumn { if (it.isPrivate) "Private" else "Public" }.setHeader("Visibility")
            
            setItems(emptyList())
            setSizeFull()
            
            try {
                val projects = api.getProjects()
                setItems(projects)
            } catch (e: Exception) {
                // API unavailable - show empty grid
            }
        }
    }
}
