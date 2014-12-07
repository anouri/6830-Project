$(document).ready(function() {

	var tables_to_fields = $("#tables_to_fields");

	var query_type = $("#query_type");
	var tables = $("#tables");

	var update_tag = $("#update_tag");
	var update_fields = $(".update_fields");
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


	var listing = [update_tag, update_fields, select_fields_tag, select_fields, where_tag, add_predicate_button, predicates, groupby_tag, groupby_fields, orderby_tag, orderby_fields, orderby_direction];

	hide_listing(listing);

	query_type.on("change", function() {
		hide_listing(listing);
		predicates.empty();
		var type = $(this).val();
		if (type == "SELECT") {
			tables.attr("multiple", true);
		} else {
			tables.attr("multiple", false);	
			if (type == "DELETE") {
				show_where_tag_and_predicates();
			}
		}
	});

	tables.on("change", function() {
		hide_listing(listing);
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
			update_tag.show();
			$("#update_fields-"+$(this).val()).show();
			show_where_tag_and_predicates();
		} else if (query_type.val() == "DELETE") {
			show_where_tag_and_predicates();
		}
	})

	select_fields.on("change", function() {
		show_where_tag_and_predicates();
		show_groupby_orderby();
	})

	add_predicate_button.click(function(e) {
		e.preventDefault();
		add_predicate_form(jQuery.parseJSON(tables_to_fields.val()), $("#predicates div").length);
	})

	predicates.on("click", ".remove_field", function(e) {
		e.preventDefault();
		$(this).parent('div').remove();
	})

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

	function add_predicate_form(tables_to_fields, index) {
		var html_to_append = '<div>';
		var prefix = '<select id="' + index + '-prefix" name="' + index + '-prefix"><option value="AND">AND</option><option value="OR">OR</option></select>';
		var operator = '<select id="' + index + '-operator" name="' + index + '-operator"><option value="=">=</option><option value="!=">!=</option><option value=">">&gt;</option><option value="<">&lt;</option><option value=">=">&gt;=</option><option value="<=">&lt;=</option></select>';
		var field1 = '<select id="' + index + '-field1" name="' + index + '-field1">';
		var field2 = '<select id="' + index + '-field2" name="' + index + '-field2"><option value="?">?</option>';
		var remove_field = '<a href="#" class="remove_field">Remove</a>'

		if (query_type.val() == "SELECT") {
			var table_names = tables.val();
			for (var i = 0; i < table_names.length; i++) {
				var fields = tables_to_fields[table_names[i]];
				for (var display_field_name in fields) {
					var option = '<option value="' + fields[display_field_name] + '">' + display_field_name + '</option>';
					field1 += option;
					field2 += option;				
				}
				// for (var j = 0; j < fields.length; j++) {
				// 	var option = '<option value="' + fields[j] + '">' + fields[j] + '</option>';
				// 	field1 += option;
				// 	field2 += option;
				// }

			}
		}	else {
			var fields = tables_to_fields[tables.val()];
			for (var display_field_name in fields) {
				var option = '<option value="' + fields[display_field_name] + '">' + display_field_name + '</option>';
				field1 += option;
				field2 += option;				
			}
			// for (var i = 0; i < fields.length; i++) {
			// 	var option = '<option value="' + fields[i] + '">' + fields[i] + '</option>';
			// 	field1 += option;
			// 	field2 += option;
			// }
		}
		field1 += '</select>';
		field2 += '</select>';
		
		if (index == 0) {
			html_to_append += field1 += operator += field2 += remove_field += '</div>';
		} else {
			html_to_append += prefix += field1 += operator += field2 += remove_field += '</div>';
		}
		predicates.append(html_to_append);
	}

	function hide_listing(listing) {
		for (var i = 0; i < listing.length; i++) {
			listing[i].hide();
		}
	}

	function show_where_tag_and_predicates() {
		where_tag.show();
		add_predicate_button.show();
		predicates.show();
	}

	function show_groupby_orderby() {
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

});