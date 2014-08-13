/* Convert mulitselect to selectable span buttons and map selected buttons to selected in multiselect
 * @author: Mikołaj Sochacki  mikolajsochacki  at gmail com
 * Licence: MIT
 * */

jQuery.fn.multiToButtons = function(options) {

	var plugin = this;
	var options = options;

	plugin.settings = {
		activeClass : 'selectedTag',
		inactiveClass : 'unselectedTag',
		maxSelection : options.max,
	}
	plugin.working = false;

	plugin.selectedValue = new Array();
    plugin.mapOfTagValue = new Object();
	plugin.multiSelect = plugin.children('select').first();

	plugin.init = function() {

		if (options != undefined) {
			if (options.activeClass != undefined)
				plugin.settings.activeClass = options.activeClass;
			if (options.inactiveClass != undefined)
				plugin.settings.inactiveClass = options.inactiveClass;
			if (options.maxSelection != undefined)
				plugin.settings.maxSelection = options.maxSelection;
		}

		plugin.multiSelect.hide();

        plugin.makeMapOfSelect();

		plugin.multiSelect.children('option').each( function() {
			var tab = document.createElement('span');
			var option = $(this);
			var value = option.val();
			var label= option.text();
			var addClass = "";
			if (option.attr('selected') != undefined && option.attr('selected') == 'selected') {
				addClass = plugin.settings.activeClass;
				plugin.selectedValue.push(value);
			} else
				addClass = plugin.settings.inactiveClass;
				$(tab).html(label).addClass(addClass);
				plugin.append(tab);
		});

		plugin.children('span').each( function() {
			$(this).click(function() {
				var $span = $(this);
				var tag = $span.html();
                var ID = plugin.mapOfTagValue[tag];
               
				if ($span.hasClass('selectedTag')) {
					$span.removeClass('selectedTag').addClass('unselectedTag');
					var i = plugin.selectedValue.indexOf(tag);
					if (i > -1)	plugin.selectedValue.splice(i, 1);
					plugin.unmarkSelected(ID);
				} else {
					if(plugin.selectedValue.length >= plugin.settings.maxSelection) {
						var toRemoveSpan = plugin.children('span.selectedTag').first();
						toRemoveSpan.removeClass('selectedTag').addClass('unselectedTag');
						var toRemoveTag = toRemoveSpan.html();
                        var toRemoveID = plugin.mapOfTagValue[toRemoveTag];
						var i = plugin.selectedValue.indexOf(toRemoveTag);
						if (i > -1)	plugin.selectedValue.splice(i, 1);
						plugin.unmarkSelected(toRemoveID);
			        }
				    plugin.selectedValue.push(tag);
    			    $span.removeClass('unselectedTag').addClass('selectedTag');
				    plugin.markSelected(ID);		
			    }
			});
		});

	}

	plugin.markSelected = function(value) {
		plugin.multiSelect.children('option').each(function() {
			if (this.value == value) {
				//alert("Found tag " + tag + " and add selected");
				$(this).attr('selected', "selected");
			}
		});
	}

	plugin.unmarkSelected = function(value) {
		plugin.multiSelect.children('option').each(function() {
			if (this.value == value) {
				//alert("Found tag " + tag + " and remove selected");
				$(this).removeAttr('selected');
			}
		});
	}

    plugin.makeMapOfSelect = function() {
        plugin.multiSelect.children("option").each(function(){
            plugin.mapOfTagValue[this.innerHTML] = this.value;
        });
    }

	plugin.init();

}
