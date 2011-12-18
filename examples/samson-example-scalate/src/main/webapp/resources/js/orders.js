$(function() {
	var dateOptions = {
			dateFormat: "dd M yy",
			showOn: "button",
			buttonImageOnly: true
		};

	$("#orderDate").datepicker(dateOptions);
	$("#shipDate").datepicker(dateOptions);

	$("#orderForm").submit(function() {
		var i = 0;

		return true;
	});
});
