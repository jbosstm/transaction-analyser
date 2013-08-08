const POLL_INTERVAL = 5000


$(document).ready(
    function refreshPage()
    {
        setTimeout(
            function() {
                $('body').load(document.URL);
                refreshPage();
            },
            POLL_INTERVAL);

    }
);