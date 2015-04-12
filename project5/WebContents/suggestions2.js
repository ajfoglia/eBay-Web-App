
/**
 * Provides suggestions for state names (USA).
 * @class
 * @scope public
 */
function StateSuggestions() {
    this.aSuggestions = [];
}

/**
 * Request suggestions for the given autosuggest control. 
 * @scope protected
 * @param oAutoSuggestControl The autosuggest control to provide suggestions for.
 */
StateSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl /*:AutoSuggestControl*/,
                                                          bTypeAhead /*:boolean*/) {
    
    
    // Get text currently in field
    var sTextboxValue = oAutoSuggestControl.textbox.value;
    
    if (sTextboxValue.length > 0){
    
        // Fetch results via proxy server
        var request = new XMLHttpRequest();
        var reqObjThis = this;

        request.onreadystatechange = function() {
            if (request.readyState == 4 && request.status == 200) {
                // Parse the response XML
                var suggestions = reqObjThis.filterSuggestions(request.responseXML);
                this.aSuggestions = suggestions;
                oAutoSuggestControl.autosuggest(this.aSuggestions, bTypeAhead);
            }
        }

        // Use google suggest
        var suggestURL = "/eBay/suggest?q=" + sTextboxValue;
        request.open("get", suggestURL, true);
        request.send();
    }

    //provide suggestions to the control
    oAutoSuggestControl.autosuggest(this.aSuggestions, bTypeAhead);
};

StateSuggestions.prototype.filterSuggestions = function (response) {
    
    // Array to store results in
    var googleSuggestions = Array();

    // If undefined results from google then no suggestions
    if (typeof response === "undefined") 
        return [];

    // Retrieve child nodes of google suggest (XML)
    var completeSuggestions = response.documentElement.childNodes;

    // Iterate and grab "data" field (XML format) in each suggestion returned by Google
    for(var i = 0; i < completeSuggestions.length; i++) {
        googleSuggestions[i] = completeSuggestions[i].childNodes[0].getAttribute("data"); 
    }

    // Return desired suggestions
    return googleSuggestions;

};