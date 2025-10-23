(function () {
    var page = 0, size = 10;

    function formatDate(iso) {
        return new Date(iso).toLocaleString();
    }

    function loadMessages() {
        const sender = $('#filterSender').val();
        $.get('/api/messages', { page, size, sender }, function(response) {
            renderTable(response.data);
        });
    }

    function renderTable(data) {
        const $tbody = $('#messagesTable tbody').empty();

        data.content.forEach(m => {
            const $tr = $('<tr/>');
            $tr.append(`<td>${m.sender}</td>`);
            $tr.append(`<td>${m.text}</td>`);
            $tr.append(`<td>${formatDate(m.createdAt)}</td>`);

            const $actions = $('<td class="actions"/>');
            $actions.append(`<button class="edit-btn">Edit</button>`);
            $actions.append(`<button class="delete-btn">Delete</button>`);

            $actions.find('.edit-btn').click(() => {
                const newText = prompt("Edit message:", m.text);
                if (newText) updateMessage(m.id, m.sender, newText);
            });

            $actions.find('.delete-btn').click(() => {
                if (confirm("Are you sure?")) deleteMessage(m.id);
            });

            $tr.append($actions);
            $tbody.append($tr);
        });

        $('#pageInfo').text(`Page ${data.number + 1} of ${data.totalPages}`);
        $('#prevPage').prop('disabled', data.first);
        $('#nextPage').prop('disabled', data.last);
    }

    function updateMessage(id, sender, newText) {
        $.ajax({
            url: `/api/messages/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ sender, text: newText }),
            success: loadMessages
        });
    }

    function deleteMessage(id) {
        $.ajax({
            url: `/api/messages/${id}`,
            method: 'DELETE',
            success: loadMessages
        });
    }

    $('#createForm').submit(function (e) {
        e.preventDefault();
        const sender = $('#sender').val();
        const text = $('#text').val();
        $.post('/api/messages', JSON.stringify({ sender, text }), function() {
            $('#text').val('');
            loadMessages();
        }, 'json');
    });

    $('#reloadBtn').click(loadMessages);
    $('#prevPage').click(() => { page--; loadMessages(); });
    $('#nextPage').click(() => { page++; loadMessages(); });

    loadMessages();
})();
