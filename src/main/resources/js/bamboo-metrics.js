AJS.toInit(function () {
    // Define form elements needed for this script
    let myFlag = undefined;
    const testConnection = AJS.$("#test-connection");
    const addNewServer = AJS.$("#add-configuration");

    const url = AJS.contextPath() + "/rest/metrics/1.0/";


    document.addEventListener('aui-flag-container', function() {
        myFlag.close();
    });
    // Remove buttons, and only make them available when input have been initialized
    addNewServer.hide();

    addNewServer.click( function(event) {
        const serverName = AJS.$("#serverName").val();
        const description = AJS.$("#description").val();
        const apiKey = AJS.$("#apiKey").val();
        const appKey = AJS.$("#appKey").val();
        addNewServer.attr("disabled", true);
        addNewServer.text("Testing...");
        event.preventDefault();
        AJS.$.ajax({
            url: url, // Update with your endpoint
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({serverName, description, apiKey, appKey}),
            success: function (response) {
                if (response.success) {
                    myFlag = AJS.flag({
                        type: 'success',
                        body: 'Issue <strong>Server</strong> has been added.'
                    });
                    testConnection.show();
                    addNewServer.hide()
                } else {
                    myFlag = AJS.flag({
                        type: 'error',
                        body: 'Failed <strong>to add metric </strong> server.'
                    });
                    addNewServer.text("Test Connection");
                    addNewServer.attr("enable", true);
                }
            },
            error: function () {
                myFlag = AJS.flag({
                    type: 'error',
                    body: '<strong>An error occurred</strong>.'
                });
            }
        });
    });
    testConnection.click(function (event) {
        event.preventDefault();
        const apiKey = AJS.$("#apiKey").val();
        const appKey = AJS.$("#appKey").val();
        testConnection.attr("disabled", true)
        testConnection.text("Testing...");
        AJS.$.ajax({
            url: url+`connection`, // Update with your endpoint
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({apiKey, appKey}),
            success: function (response) {
                if (response.passed) {
                    testConnection.hide()
                    myFlag = AJS.flag({
                        type: 'success',
                        body: response.message
                    });
                    testConnection.hide();
                    addNewServer.show()
                } else {
                     AJS.flag({
                        type: 'error',
                        body: response.message
                    });
                    testConnection.attr("disabled", false)
                    testConnection.text("Test Connection");
                }
            },
            error: function () {
                AJS.flag({
                    type: 'error',
                    body: response.message
                });
            }
        });

    });
    
    AJS.$('.radio input[type="checkbox"]').click( ()=>{
        console.log("Checked")
    })

});

function setDefaultServer(currentCheckbox, serverId) {
    const url = AJS.contextPath() + "/rest/metrics/1.0/";

    const secure_token = document.getElementsByName("atl_token")
    console.log(secure_token)
    // Get all checkboxes with the class "radio"
    const checkboxes = document.querySelectorAll(".radio");
    // Loop through all checkboxes
    checkboxes.forEach((checkbox) => {
        // Uncheck all other checkboxes
        if (checkbox !== currentCheckbox) {
            checkbox.checked = false;
        }
    });
    // Log the selected server (optional)
    AJS.$.ajax({
        url: url+`default/server/${serverId}`, // Update with your endpoint
        type: "POST",
        contentType: "application/json",
        success: function (response) {
            console.log(response)
        },
        error: function (error) {
            console.log(error.responseText)
        }
    });
}


