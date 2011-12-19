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
		$( ".samson-item > .input" ).each( function() {

			var $orderId = $( this ).children( "#orderId" ),
				$productId = $( this ).children( "#productId" );

			var $name = $( this ).children( "#name" ),
				$qty = $( this ).children( "#qty" );

			var itemName = "order.items[" + i + "]";

			// Set composite key
			$orderId.attr( "name", itemName + ".order.id" );
			$productId.attr( "name", itemName + ".product.id" );

			$name.attr( "name", itemName + ".product.name" );
			$qty.attr( "name", itemName + ".qty" );

			i++;
		});

		return true;
	});
});
