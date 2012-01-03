$(function() {
	var dateOptions = {
			dateFormat: "dd M yy",
			showOn: "button",
			buttonImageOnly: true
		};

	$( "#orderDate" ).datepicker( dateOptions );
	$( "#shipDate" ).datepicker( dateOptions );

	$( "#orderForm" ).submit( function() {
		var i = 0;

		// Fill-in the name attribute of each item's input elements
		$( "div.order-item-edit > .input" ).each( function() {
			var $itemInput = $( this ),
				item = "items[" + i + "]";

			$itemInput
				.children( "#id" ).attr( "name", item + "." + "product.id" ).end()
				.children( "#name" ).attr( "name", item + "." + "product.name" ).end()
				.children( "#qty" ).attr( "name", item + "." + "qty" ).end();

			i++;
		});
	});

	$( "div.order-item-edit button#del" ).live( "click", function() {
		$( this ).closest( "div.order-item-edit" ).fadeOut( "fast", function() {
			$( this ).remove();
		});
	});

	$( "div.order-items-new button#add" ).click( function() {
		var $item = $( this ),
			$itemInput = $item.parent( ".input" ),
			$tmpl = $( "div.order-item-edit-tmpl" ).clone().removeClass( "order-item-edit-tmpl" ).addClass( "order-item-edit" ),
			$tmplInput = $tmpl.children( ".input" );

		var $product = $itemInput.find( "#product > option" ).filter( ":selected" ),
			$qty = $itemInput.children( "#qty" );

		// Don't do any conversion/validation here in order to demonstrate server-side features

		$tmplInput
			.children( "#id" ).val( $product.val() ).end()
			.children( "#name" ).val( $product.text() ).end()
			.children( "#nameText" ).val( $product.text() ).end()
			.children( "#qty" ).val( $qty.val() ).end();

		$( "div.order-items" ).append( $tmpl.hide().fadeIn( "slow" ) );
	});
});
