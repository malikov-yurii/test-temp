var datatableApi;

$(document).ready(function () {
    datatableApi = $('#datatable').DataTable({
        "ajax": {
            "url": ajaxUrl,
            "dataSrc": ""
        },
        "searching": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "columns": [
            {"data": "id", "orderable": false},
            {"data": "departureAirport", "orderable": false},
            {"data": "arrivalAirport", "orderable": false},
            {"data": "departureLocalDateTime", "className": "input-datetime", "orderable": false},
            {"data": "arrivalLocalDateTime", "className": "input-datetime", "orderable": false},
            {"data": "aircraftName", "orderable": false},
            {"data": "initialBaseTicketPrice", "orderable": false},
            {"data": "maxBaseTicketPrice", "orderable": false},
            {"orderable": false, "render": renderUpdateBtn},
            {"orderable": false, "render": renderDeleteBtn}
        ],
        "initComplete": onFlightTableReady,
        "order": [
            [
                0,
                "desc"
            ]
        ],
        "autoWidth": false
    });

    datatableApi.on('click', '.update-btn', showUpdateModal);

    $('.input-datetime').datetimepicker({
        format: getDateTimePickerFormat()
        // , startDate: new Date()
        , minDate: 0
        // , startTime: 0
        // , minTime: 0
    });

    $('.input-airport').autocomplete({source: 'ajax/profile/airport/autocomplete-by-name'});

    $('.input-aircraft').autocomplete({source: 'ajax/profile/aircraft/autocomplete-by-name'});

    $('.input-airport, .input-aircraft')
        .on("autocompleteselect",
            function (event, ui) {
                var $this = $(this);
                $this.addClass('valid in-process');
                $this.parents().eq(1).next().find('.form-control').focus();
            }
        ).on("autocompletechange",
        function (event, ui) {
            var $this = $(this);
            $this.addClass('valid');
            if (!$this.hasClass('in-process')) {
                $this.removeClass('valid');
            }
            $this.removeClass('in-process');
        }
    ).autocomplete("option", "minLength", 0);


});

function saveFlight() {


    var message = "";
    if (!$('#aircraftName').hasClass('valid')) {
        message += 'Please select aircraft from drop-down list.';
    }

    if (!$('#departureAirport').hasClass('valid')) {
        if (message.length != 0) {
            message += '\n'
        }
        message += 'Please select departure airport from drop-down list.';
    }
    if (!$('#arrivalAirport').hasClass('valid')) {
        if (message.length != 0) {
            message += '\n'
        }
        message += 'Please select arrival airport from drop-down list.';
    }

    var currentMoment = new Date();

    if ($("#departureLocalDateTime").val().length == 0) {
        if (message.length != 0) {
            message += '\n'
        }
        message += 'Please set arrival local date time.';
    } else if (new Date($("#departureLocalDateTime").val()) < currentMoment) {
        if (message.length != 0) {
            message += '\n'
        }
        message += 'Arrival local date time cannot be earlier than ' + currentMoment.toJSON().slice(0,16).replace('T', ' ');
    }
    if ($("#arrivalLocalDateTime").val().length == 0) {
        if (message.length != 0) {
            message += '\n'
        }
        message += 'Please set arrival local date time.';
    } else if (new Date($("#arrivalLocalDateTime").val()) < currentMoment) {
        if (message.length != 0) {
            message += '\n'
        }
        message += 'Departure local date time cannot be earlier than ' + currentMoment.toJSON().slice(0,16).replace('T', ' ');
    }

    if (message.length != 0) {
// todo replace alert with jquery popup
        alert(message);
    } else {

        $('.valid').removeClass('valid reselected');
        alert('success');

    }


    // $.ajax({
    //     type: "POST",
    //     url: ajaxUrl,
    //     data: $('#detailsForm').serialize(),
    //     success: function () {
    //         $('#editRow').modal('hide');
    //         updateTable();
    //         successNoty('common.saved');
    //     }
    // });
}

function onFlightTableReady() {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
}

function showAddFlightModal() {
    $('#modalTitle').html('Add new ' + entityName);
    $('#id').val(0);
    $('#departureAirport').val('');
    $('#arrivalAirport').val('');
    $('#departureLocalDateTime').val('');
    $('#arrivalLocalDateTime').val('');
    $('#aircraftName').val('');
    $('#initialBaseTicketPrice').val('');
    $('#maxBaseTicketPrice').val('');
    $('#editRow').modal();
}


