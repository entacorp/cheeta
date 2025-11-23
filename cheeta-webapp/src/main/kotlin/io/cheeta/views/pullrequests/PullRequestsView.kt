package io.cheeta.views.pullrequests

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.router.Route
import io.cheeta.MainLayout
import io.cheeta.api.client.CheetaApiClient
import io.cheeta.api.dto.PullRequestDto
import org.springframework.beans.factory.annotation.Autowired

@Route("pullrequests", layout = MainLayout::class)
class PullRequestsView(
    @Autowired private val api: CheetaApiClient
) : Div() {
    
    init {
        setSizeFull()
        style.set("padding", "20px")
        
        add(
            H1("Pull Requests").apply {
                style.set("color", "#F59E0B")
            },
            createPRsGrid()
        )
    }

    private fun createPRsGrid(): Grid<PullRequestDto> {
        return Grid<PullRequestDto>().apply {
            addColumn { it.number?.toString() ?: "-" }.setHeader("#").setWidth("80px")
            addColumn { it.title }.setHeader("Title").setFlexGrow(2)
            addColumn { it.sourceBranch }.setHeader("Source").setFlexGrow(1)
            addColumn { it.targetBranch }.setHeader("Target").setFlexGrow(1)
            addColumn { it.state }.setHeader("State").setWidth("100px")
            
            setItems(emptyList())
            setSizeFull()
        }
    }
}
