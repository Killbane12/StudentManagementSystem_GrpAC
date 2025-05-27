// General purpose JavaScript file for basic DOM manipulation or non-AJAX enhancements.
// Specific AJAX calls might be better placed in the JSPs that use them,
// or organized into modules if the application grows very large.

document.addEventListener('DOMContentLoaded', function() {
    console.log("SMS JavaScript loaded.");

    // Example: Confirm before delete
    const deleteLinks = document.querySelectorAll('a.delete, button.delete');
    deleteLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            if (!confirm('Are you sure you want to delete this item?')) {
                event.preventDefault();
            }
        });
    });

    // Example: Make table rows clickable if they have a data-href attribute
    const clickableRows = document.querySelectorAll('tr[data-href]');
    clickableRows.forEach(row => {
        row.addEventListener('click', function() {
            window.location.href = this.dataset.href;
        });
        row.style.cursor = 'pointer';
    });

    // Handle flash messages (if any) - hide after some time
    const flashMessages = document.querySelectorAll('.message.auto-hide');
    flashMessages.forEach(function(msg){
        setTimeout(function(){
            msg.style.display = 'none';
        }, 5000); // Hide after 5 seconds
    });

});

// Function for NFC Simulator's "Randomize Student" (can be called from nfc_attendance_simulator.jsp)
function fetchRandomUnmarkedStudent(lectureSessionId, contextPath) {
    const studentIdField = document.getElementById('studentIdToMark');
    const statusMessageDiv = document.getElementById('nfcStatusMessage'); // Assuming you have a div for messages
    statusMessageDiv.textContent = ''; // Clear previous messages
    statusMessageDiv.className = 'nfc-status-message';


    if (!lectureSessionId) {
        studentIdField.value = '';
        statusMessageDiv.textContent = 'Please select an active lecture session first.';
        statusMessageDiv.classList.add('error');
        return;
    }

    studentIdField.value = 'Fetching...';

    // Ensure contextPath ends with a slash if it's not empty and doesn't have one.
    // For a servlet in the root like /SimulatedAttendancePunchServlet, contextPath might be all that's needed.
    // If SimulatedAttendancePunchServlet handles "getRandomStudent" action:
    const url = `${contextPath}/SimulatedAttendancePunchServlet?action=getRandomStudent&lectureSessionId=${lectureSessionId}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.studentId) {
                studentIdField.value = data.studentId;
                if(data.message) { // Optional success message from server
                    statusMessageDiv.textContent = data.message;
                    statusMessageDiv.classList.add('success');
                }
            } else {
                studentIdField.value = '';
                statusMessageDiv.textContent = data.message || 'No unmarked students found or error fetching.';
                statusMessageDiv.classList.add(data.error ? 'error' : 'info'); // Use 'info' if just no students
            }
        })
        .catch(error => {
            console.error('Error fetching random student:', error);
            studentIdField.value = '';
            statusMessageDiv.textContent = 'Error fetching student. Check console for details.';
            statusMessageDiv.classList.add('error');
        });
}