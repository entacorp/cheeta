package io.cheeta

import com.vaadin.flow.component.AppLayout
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Header
import com.vaadin.flow.component.html.Nav
import com.vaadin.flow.component.html.UnorderedList
import com.vaadin.flow.component.html.ListItem
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.theme.lumo.LumoUtility

class MainLayout : AppLayout() {
    init {
        createHeader()
        createDrawer()
    }

    private fun createHeader() {
        val header = Header()
        header.addClassNames(LumoUtility.Background.PRIMARY, LumoUtility.Padding.MEDIUM)
        header.add(H2("🐆 Cheeta"))
        setNavbar(header)
    }

    private fun createDrawer() {
        val nav = Nav()
        val list = UnorderedList()

        listOf(
            "Home" to "/",
            "Dashboard" to "/dashboard",
            "Projects" to "/projects",
            "Issues" to "/issues",
            "Pull Requests" to "/pullrequests",
            "Builds" to "/builds",
            "DevFeed" to "/feed",
            "Jobs" to "/jobs",
            "Sponsors" to "/sponsors",
            "Packages" to "/packages",
            "Pages" to "/pages",
            "Analytics" to "/analytics",
            "Templates" to "/templates"
        ).forEach { (label, path) ->
            list.add(
                ListItem(RouterLink(label, path).apply {
                    style.set("padding", "8px")
                    style.set("display", "block")
                    style.set("text-decoration", "none")
                    style.set("color", "var(--lumo-text-color)")
                })
            )
        }

        nav.add(list)
        nav.style.set("padding", "10px")
        addToDrawer(nav)
    }
}
