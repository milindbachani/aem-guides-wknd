(function () {
    /**
     * Fetches form data using guideBridge and performs an AJAX call to submit it.
     */
    function submitFormData() {
        // Ensure guideBridge is available
        if (typeof guideBridge === "undefined") {
            console.error("guideBridge is not defined.");
            alert("Unable to submit form. GuideBridge is not initialized.");
            return;
        }

        // Fetch form data
        guideBridge.getData({
            success: function (guideResultObject) {
                try {
                    let tempObj = JSON.parse(guideResultObject.data);

                    if (tempObj?.afData?.afBoundData?.data) {
                        const formData = tempObj.afData.afBoundData.data;

                        // Perform AJAX call
                        var xhr = new XMLHttpRequest();
                        xhr.open("POST", "/bin/push-data.json", true);
                        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status === 200) {
                                    // Handle success response
                                    alert("Form submitted successfully: " + xhr.responseText);
                                } else {
                                    // Handle error response
                                    console.error("Error submitting form. Status: " + xhr.status);
                                    alert("Error submitting form. Please try again later.");
                                }
                            }
                        };

                        // Send form data as JSON
                        xhr.send(JSON.stringify(formData));
                    } else {
                        console.error("Form data is unavailable or malformed.");
                        alert("Unable to submit form. Data is not properly structured.");
                    }
                } catch (error) {
                    console.error("Error processing form data:", error);
                    alert("An error occurred while processing the form. Please try again.");
                }
            },
            error: function (error) {
                console.error("Error fetching form data via guideBridge:", error);
                alert("Unable to fetch form data. Please ensure all fields are correctly filled.");
            }
        });
    }

    // Expose the function globally if needed for external event handlers
    window.submitFormData = submitFormData;
})();
