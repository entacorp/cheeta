cheeta.server.issueChoiceFormatter = {
	formatIssue: function(issue) {
		return issue.title + " (" + issue.reference +")";
	},
	formatSelection: function(issue) {
		return cheeta.server.issueChoiceFormatter.formatIssue(issue);
	},
	formatResult: function(issue) {
		return cheeta.server.issueChoiceFormatter.formatIssue(issue);
	},
	escapeMarkup: function(m) {
		return m;
	},
	
}; 
