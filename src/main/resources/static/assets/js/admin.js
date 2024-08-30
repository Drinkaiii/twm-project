function toggleCategoryFields() {
    const categoryType = document.getElementById('category-type').value;
    document.getElementById('general-category-fields').classList.add('hidden');
    document.getElementById('faq-category-fields').classList.add('hidden');
    document.getElementById('questions-answers-section').classList.add('hidden');
    document.getElementById('submit-section-general').classList.add('hidden');
    document.getElementById('submit-section-faq-qa').classList.add('hidden');
    document.getElementById('submit-section-faq-type').classList.add('hidden');
    document.getElementById('category-warning').classList.add('hidden');

    document.getElementById('category-table').innerHTML = '';

    if (categoryType === 'general') {
        document.getElementById('general-category-fields').classList.remove('hidden');
        document.getElementById('submit-section-general').classList.remove('hidden');

        fetchCategoriesAndDisplayTable();
    } else if (categoryType === 'faq') {
        document.getElementById('faq-category-fields').classList.remove('hidden');
        document.getElementById('category-warning').classList.remove('hidden');
        document.getElementById('submit-section-faq-qa').classList.remove('hidden');
        document.getElementById('questions-answers-section').classList.remove('hidden');
    }
}


function fetchCategoriesAndDisplayTable() {
    fetch('api/1.0/chat/routines')
        .then(response => response.json())
        .then(data => {
            const categories = data.data;
            let tableHtml = `
                <table class="styled-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>類別名稱</th>
                            <th>連結</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            categories.forEach(category => {
                tableHtml += `
                    <tr>
                        <td>${category.id}</td>                    
                        <td>${category.category}</td>
                        <td>${category.url}</td>
                        <td>
                            <button type="button" class="update-button" onclick="updateCategory(${category.id})">更新</button>
                            <button type="button" class="delete-button" onclick="deleteCategory(${category.id})">删除</button>
                        </td>
                    </tr>
                `;
            });

            tableHtml += `
                    </tbody>
                </table>
            `;

            document.getElementById('category-table').innerHTML = tableHtml;
        })
        .catch(error => console.error('Error fetching categories:', error));
}

function showFaqSelect() {
    document.getElementById('faq-category-name').value = '';
    document.getElementById('faq-select-existing').classList.remove('hidden');
    document.getElementById('faq-add-new').classList.add('hidden');
    document.getElementById('submit-section-faq-qa').classList.remove('hidden');
    document.getElementById('submit-section-faq-type').classList.add('hidden');
    document.getElementById('questions-answers-section').classList.remove('hidden');
    document.getElementById('all-type-table').classList.add('hidden');

    fetchCategories();
    checkCategorySelection();
}

function showFaqAdd() {
    document.getElementById('faq-category-select').value = '';
    document.getElementById('faq-select-existing').classList.add('hidden');
    document.getElementById('faq-add-new').classList.remove('hidden');
    document.getElementById('submit-section-faq-qa').classList.add('hidden');
    document.getElementById('submit-section-faq-type').classList.remove('hidden');
    document.getElementById('questions-answers-section').classList.add('hidden');
    document.getElementById('type-table').innerHTML = '';

    checkCategorySelection();
    handleTypeAndDisplayTable()
}

function addQuestionAnswer() {
    const container = document.getElementById('question-answer-container');
    const index = container.children.length;
    const div = document.createElement('div');
    div.classList.add('question-answer-group');
    div.innerHTML = `
                <label for="question-${index}">問題：</label>
                <input type="text" id="question-${index}" name="questions[]">
                <br>
                <label for="answer-${index}">答案：</label>
                <input type="text" id="answer-${index}" name="answers[]">
            `;
    container.appendChild(div);

    checkCategorySelection();
}

function checkCategorySelection() {
    const categorySelect = document.getElementById('faq-category-select').value;
    const categoryName = document.getElementById('faq-category-name').value;
    const inputs = document.querySelectorAll('#question-answer-container input[type="text"]');
    const categoryWarning = document.getElementById('category-warning');

    if (categorySelect !== "" || categoryName.trim() !== "") {
        inputs.forEach(input => input.disabled = false);
        categoryWarning.classList.add('hidden');
    } else {
        inputs.forEach(input => input.disabled = true);
        const categoryWarning = document.getElementById('category-warning');
        categoryWarning.classList.remove('hidden');
    }
}

let token;
document.addEventListener("DOMContentLoaded", function () {
    fetchCategories();
    token = localStorage.getItem('jwtToken');
});

function fetchCategories() {
    fetch('api/1.0/chat/routines?category=4')
        .then(response => response.json())
        .then(data => {
            populateCategorySelect(data.data);
            populateTypeSelect(data.data);
        })
        .catch(error => console.error('Error fetching categories:', error));
}

let typeId;

function populateCategorySelect(categories) {
    const select = document.getElementById('faq-category-select');
    select.innerHTML = '<option value="">--請選擇--</option>';

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.type_name;
        select.appendChild(option);
    });

    select.addEventListener('change', function () {
        console.log(select);
        typeId = select.value;

        handleCategoriesAndDisplayTable(typeId);
    });
}

let currentQuestions = [];

function handleCategoriesAndDisplayTable(typeId) {
    fetch(`api/1.0/admin/chat/review`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('type-table').classList.remove('hidden');
            const filteredData = data.data.filter(item => item.type === parseInt(typeId));

            if (filteredData.length === 0) {
                document.getElementById('type-table').classList.add('hidden');
                return;
            }

            currentQuestions = filteredData;

            let tableHtml = `
            <table class="styled-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>問題</th>
                        <th>答案</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
        `;

            filteredData.forEach(question => {
                tableHtml += `
                <tr>
                    <td>${question.id}</td>                    
                    <td>${question.question}</td>
                    <td>${question.answer}</td>
                    <td>
                        <button type="button" class="update-button" onclick="updateQuestion(${question.id})">編輯</button>
                        <button type="button" class="delete-button" onclick="deleteQuestion(${question.id})">删除</button>
                    </td>
                </tr>
            `;
            });

            tableHtml += `
                </tbody>
            </table>
        `;

            document.getElementById('type-table').innerHTML = tableHtml;
            document.getElementById('submit-section-faq-qa').classList.remove('hidden');
        })
        .catch(error => console.error('Error fetching questions:', error));
}

let selectedId;

function updateQuestion(id) {
    selectedId = id;
    const questionData = findQuestionById(id);

    document.getElementById('question-input').value = questionData.question;
    document.getElementById('answer-input').value = questionData.answer;

    document.getElementById('type-select').value = questionData.type;
    document.getElementById('updateModal').style.display = 'block';
}

function findQuestionById(id) {
    return currentQuestions.find(question => question.id === id);
}

function populateTypeSelect(categories) {
    const select = document.getElementById('type-select');
    select.innerHTML = '<option value="">--請選擇--</option>';

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.type_name;
        select.appendChild(option);
    });

    select.addEventListener('change', function () {
        document.getElementById('type-select').value = select.value;
    });
}

function closeModal() {
    document.getElementById('updateModal').style.display = 'none';
}

function saveQuestion() {
    const updatedType = document.getElementById('type-select').value;
    const updatedQuestion = document.getElementById('question-input').value;
    const updatedAnswer = document.getElementById('answer-input').value;

    fetch(`api/1.0/admin/chat/update`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: selectedId,
            type: updatedType,
            question: updatedQuestion,
            answer: updatedAnswer
        })
    }).then(response => {
        if (response.ok) {
            alert('更新成功');
            closeModal();
            handleCategoriesAndDisplayTable(updatedType);
        } else {
            alert('更新失敗');
        }
    }).catch(error => {
        console.error('Error updating question:', error);
    });

    closeModal();
}

function deleteQuestion(id) {
    selectedId = id;
    fetch(`api/1.0/admin/chat/delete`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: selectedId
        })
    }).then(response => {
        if (response.ok) {
            closeModal();
            handleCategoriesAndDisplayTable(typeId);
        } else {
            alert('刪除失敗');
        }
    }).catch(error => {
        console.error('Error updating question:', error);
    });

}

function submitFaqQa() {
    const selectedCategory = Number(document.getElementById('faq-category-select').value);
    const questionAnswerPairs = document.querySelectorAll('.question-answer-group');

    if (!selectedCategory) {
        alert("請選擇類別！");
        return;
    }
    //deal with input questions and answers
    for (let i = 0; i < questionAnswerPairs.length; i++) {
        const question = questionAnswerPairs[i].querySelector('input[name="questions[]"]').value.trim();
        const answer = questionAnswerPairs[i].querySelector('input[name="answers[]"]').value.trim();

        if (question === "" && answer === "") { //pass
            continue;
        } else if (question === "" || answer === "") { //both answer and question should be input
            alert("請確保每一組問題和答案都已填寫");
            return;
        }

        const payload = {
            type: selectedCategory,
            question: question,
            answer: answer
        };

        fetch('api/1.0/admin/chat/create', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (response.ok) {
                    handleCategoriesAndDisplayTable(typeId);
                    return {};
                } else {
                    throw new Error('Network response was not ok');
                }
            })
            .then(data => {
                alert('已成功儲存類別及問答！');
                console.log('Success:', data);
            })
            .catch(error => console.error('Error:', error));
    }
}

function submitFaqType() {
    const categoryName = document.getElementById('faq-category-name').value;

    if (!categoryName) {
        alert('請輸入類別名稱');
        return;
    }

    createCategoryType(categoryName);
}

function createCategoryType(categoryName) {
    return fetch('api/1.0/admin/type/create', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            type_name: categoryName
        })
    })
        .then(response => {
            if (response.ok) {
                // handleCategoriesAndDisplayTable(typeId);
                console.log("ok");
                return {};
            } else {
                throw new Error('Network response was not ok');
            }
        })
        .then(data => {
            alert('已成功儲存問題類別！');
            console.log('Success:', data);
        })
        .catch(error => console.error('Error:', error));
}

function handleTypeAndDisplayTable() {
    fetch(`api/1.0/admin/type/review?id=1`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('all-type-table').classList.remove('hidden');
            const filteredData = data.data;
            console.log(filteredData);

            // if (filteredData.length === 0) {
            //     document.getElementById('type-table').classList.add('hidden');
            //     return;
            // }

            currentQuestions = filteredData;

            let tableHtml = `
            <table class="styled-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>類別</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
        `;

            filteredData.forEach(type => {
                tableHtml += `
                <tr>
                    <td>${type.id}</td>                    
                    <td>${type.type_name}</td>
                    <td>
                        <button type="button" class="update-button" onclick="updateType(${type.id})">編輯</button>
                        <button type="button" class="delete-button" onclick="deleteType(${type.id})">删除</button>
                    </td>
                </tr>
            `;
            });

            tableHtml += `
                </tbody>
            </table>
        `;

            document.getElementById('all-type-table').innerHTML = tableHtml;
            document.getElementById('submit-section-faq-type').classList.remove('hidden');
        })
        .catch(error => console.error('Error fetching questions:', error));
}


