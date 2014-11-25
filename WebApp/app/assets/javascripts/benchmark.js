$(document).ready(function() {

	var tables_to_fields = $("#tables_to_fields");

	var query_type = $("#query_type");
	var tables = $("#tables");

	var set_tag = $("#set_tag");
	var set_fields = $(".set_fields");
	var select_fields_tag = $("#select_fields_tag");
	var select_fields = $(".select_fields");	
	var where_tag = $("#where_tag");
	var add_predicate_button = $("#add_predicate_button");
	var predicates = $("#predicates");	
	var groupby_tag = $("#groupby_tag");
	var groupby_fields = $(".groupby_fields");	
	var orderby_tag = $("#orderby_tag");
	var orderby_fields = $(".orderby_fields");	
	var orderby_direction = $("#orderby_direction");


	var listing = [set_tag, set_fields, select_fields_tag, select_fields, where_tag, add_predicate_button, predicates, groupby_tag, groupby_fields, orderby_tag, orderby_fields, orderby_direction];

	hideListing(listing);

	query_type.on("change", function() {
		hideListing(listing);
		var type = $(this).val();
		if (type == "SELECT") {
			tables.attr("multiple", true);
		} else {
			tables.attr("multiple", false);	
			if (type == "UPDATE") {
				set_tag.show();
				$("#set_fields-"+tables.val()).show();
			} else if (type == "DELETE") {
				showWhereTagAndPredicates();
			}
		}
	});

	tables.on("change", function() {
		hideListing(listing);
		predicates.empty();
		if (query_type.val() == "SELECT") {
			var table_names = $(this).val();
			if (table_names != null) {
				select_fields_tag.show();
				for (var i = 0; i < table_names.length; i++) {
					$("#select_fields-"+table_names[i]).show();
				}
			}
		} else if (query_type.val() == "UPDATE") {
			set_tag.show();
			$("#set_fields-"+$(this).val()).show();
		} else if (query_type.val() == "DELETE") {
			showWhereTagAndPredicates();
		}
	})

	select_fields.on("change", function() {
		showWhereTagAndPredicates();
		if (query_type.val() == "SELECT") {
			showGroupOrderBy();
		}
	})

	add_predicate_button.click(function(e) {
		e.preventDefault();
		addPredicateForm(jQuery.parseJSON(tables_to_fields.val()), $("#predicates div").length);
	})

	function addPredicateForm(tables_to_fields, index) {
		var htmlToAppend = '<div>';
		var prefix = '<select id="' + index + '-prefix" name="' + index + '-prefix"><option value="AND">AND</option><option value="OR">OR</option></select>';
		var operator = '<select id="' + index + '-operator" name="' + index + '-operator"><option value="=">=</option><option value="!=">!=</option><option value=">">&gt;</option><option value="<">&lt;</option><option value=">=">&gt;=</option><option value="<=">&lt;=</option></select>';
		var field1 = '<select id="' + index + '-field1" name="' + index + '-field1">';
		var field2 = '<select id="' + index + '-field2" name="' + index + '-field2"><option value="?">?</option>';
		
		if (query_type.val() == "SELECT") {
			var table_names = tables.val();
			for (var i = 0; i < table_names.length; i++) {
				var fields = tables_to_fields[table_names[i]];
				for (var j = 0; j < fields.length; j++) {
					var option = '<option value="' + fields[j] + '">' + fields[j] + '</option>';
					field1 += option;
					field2 += option;
				}
			}
		}	else {
			var fields = tables_to_fields[tables.val()];
			for (var i = 0; i < fields.length; i++) {
				var option = '<option value="' + fields[i] + '">' + fields[i] + '</option>';
				field1 += option;
				field2 += option;
			}
		}
		field1 += '</select>';
		field2 += '</select>';
		
		if (index == 0) {
			htmlToAppend += field1 += operator += field2 += '</div>';
		} else {
			htmlToAppend += prefix += field1 += operator += field2 += '</div>';
		}
		predicates.append(htmlToAppend);
	}

	function hideListing(listing) {
		for (var i = 0; i < listing.length; i++) {
			listing[i].hide();
		}
	}

	function showWhereTagAndPredicates() {
		where_tag.show();
		add_predicate_button.show();
		predicates.show();
	}

	function showGroupOrderBy() {
		groupby_tag.show();
		var table_names = tables.val();
		for (var i = 0; i < table_names.length; i++) {
			$("#groupby_fields-"+table_names[i]).show();
		}
		orderby_tag.show();
		for (var i = 0; i < table_names.length; i++) {
			$("#orderby_fields-"+table_names[i]).show();
		}
		orderby_direction.show();
	}

	$(".distribution").on("change", function() {
		var tr = $(this).closest("tr");
		if ($(this).val() == "normal") {
			tr.find(".mean").prop("disabled", false);
			tr.find(".stdv").prop("disabled", false);
			tr.find(".min").prop("disabled", true);
			tr.find(".max").prop("disabled", true);
		} else {
			tr.find(".mean").prop("disabled", true);
			tr.find(".stdv").prop("disabled", true);
			tr.find(".min").prop("disabled", false);
			tr.find(".max").prop("disabled", false);
		}
	})

});