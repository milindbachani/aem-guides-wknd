/**
 * submitFormData
 * Submits form data fetched via GuideBridge.
 */
function submitFormData() {
    if (typeof guideBridge === "undefined") {
        console.error("GuideBridge is not defined.");
        alert("Unable to submit form. GuideBridge is not initialized.");
        return;
    }

    guideBridge.getData({
        success: function (guideResultObject) {
            try {
                var guideData = JSON.parse(guideResultObject.data);
                var formData = guideData && guideData.afData && guideData.afData.afBoundData && guideData.afData.afBoundData.data;

                if (!formData) {
                    console.error("Form data is unavailable or malformed.");
                    alert("Unable to submit form. Data is not properly structured.");
                    return;
                }

                // Submit data to the server
                var xhr = new XMLHttpRequest();
                xhr.open("POST", "/bin/pushdata", true);
                xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            alert("Form submitted successfully: " + xhr.responseText);
                        } else {
                            console.error("Error submitting form. Status: " + xhr.status);
                            alert("Error submitting form. Please try again later.");
                        }
                    }
                };

                xhr.send(JSON.stringify(formData));
            } catch (e) {
                console.error("Error processing form data:", e);
                alert("An error occurred while processing the form. Please try again.");
            }
        },
        error: function (error) {
            console.error("Error fetching form data via GuideBridge:", error);
            alert("Unable to fetch form data. Please ensure all fields are correctly filled.");
        }
    });
}
