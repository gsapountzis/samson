jQuery(function( $ ) {

	var dateOptions = {
			format: "dd M yyyy",
		};

	$( "#orderDate" ).datepicker( dateOptions );
	$( "#shipDate" ).datepicker( dateOptions );

	$( "#orderForm" ).submit( function() {

		// Fill-in the name attribute of each item's input elements
		$( "div.order-item-edit > div.controls" ).each( function( i ) {
			var $itemControls = $( this ),
				item = "items[" + i + "]";

			$itemControls
				.children( "#id" ).attr( "name", item + "." + "productId" ).end()
				.children( "#name" ).attr( "name", item + "." + "product.name" ).end()
				.children( "#qty" ).attr( "name", item + "." + "qty" ).end();
		});
	});

	$( document ).on( "click", "div.order-item-edit button#del", function() {
		$( this ).closest( "div.order-item-edit" ).fadeOut( "fast", function() {
			$( this ).remove();
		});
	});

	$( "div.order-items-new button#add" ).click( function() {
		var $itemControls = $( this ).parent( ".controls" ),
			$tmpl = $( "div.order-item-edit-tmpl" ).clone().removeClass( "order-item-edit-tmpl" ).addClass( "order-item-edit" ),
			$tmplControls = $tmpl.children( ".controls" );

		var $product = $itemControls.find( "#product > option" ).filter( ":selected" ),
			$qty = $itemControls.children( "#qty" );

		// Don't do any conversion/validation here in order to demonstrate server-side features

		$tmplControls
			.children( "#id" ).val( $product.val() ).end()
			.children( "#name" ).val( $product.text() ).end()
			.children( "#nameText" ).val( $product.text() ).end()
			.children( "#qty" ).val( $qty.val() ).end();

		$( "div.order-items" ).append( $tmpl.hide().fadeIn( "slow" ) );
	});
})( jQuery );
