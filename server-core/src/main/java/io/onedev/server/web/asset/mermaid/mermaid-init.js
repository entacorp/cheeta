var mermaidTheme = cheeta.server.isDarkMode()? "dark": "default";
mermaid.mermaidAPI.initialize({theme: mermaidTheme, startOnLoad:false});