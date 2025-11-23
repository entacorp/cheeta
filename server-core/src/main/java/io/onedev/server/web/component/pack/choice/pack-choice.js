cheeta.server.packChoiceFormatter = {
	formatPack: function(pack) {
		return pack.reference;
	},
	formatSelection: function(pack) {
		return cheeta.server.packChoiceFormatter.formatPack(pack);
	},
	formatResult: function(pack) {
		return cheeta.server.packChoiceFormatter.formatPack(pack);
	},
	escapeMarkup: function(m) {
		return m;
	},
	
}; 
