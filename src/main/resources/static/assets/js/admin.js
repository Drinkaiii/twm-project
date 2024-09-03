let token;
document.addEventListener("DOMContentLoaded", function () {
    token = localStorage.getItem('jwtToken');
    solveJwt(token);
    fetchCategories();
});

function toggleCategoryFields() {
    const categoryType = document.getElementById('category-type').value;
    document.getElementById('character-fields').classList.add('hidden');
    document.getElementById('faq-category-fields').classList.add('hidden');
    document.getElementById('questions-answers-section').classList.add('hidden');
    document.getElementById('submit-section-general').classList.add('hidden');
    document.getElementById('submit-section-faq-qa').classList.add('hidden');
    document.getElementById('submit-section-faq-type').classList.add('hidden');
    document.getElementById('category-warning').classList.add('hidden');

    document.getElementById('support-table').innerHTML = '';
    document.getElementById('character-table').innerHTML = '';

    if (categoryType === 'general') {
        document.getElementById('character-fields').classList.remove('hidden');
        document.getElementById('submit-section-general').classList.remove('hidden');

        fetchCharacterAndDisplayTable();
    } else if (categoryType === 'faq') {
        document.getElementById('faq-category-fields').classList.remove('hidden');
        document.getElementById('category-warning').classList.remove('hidden');
        document.getElementById('submit-section-faq-qa').classList.remove('hidden');
        document.getElementById('questions-answers-section').classList.remove('hidden');
        document.getElementById('faq-add-new').classList.add('hidden');
    } else if (categoryType === 'support') {
        fetchSupportAndDisplayTable();
    }
}

let currentData = [];

function fetchCharacterAndDisplayTable() {
    fetch(`api/1.0/admin/personality/review`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('character-table').classList.remove('hidden');
            const filteredData = data.data;

            if (filteredData.length === 0) {
                document.getElementById('character-table').classList.add('hidden');
                return;
            }

            currentData = filteredData;

            let tableHtml = `
                <table class="styled-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>描述</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            filteredData.forEach(personality => {
                tableHtml += `
                    <tr>
                        <td>${personality.id}</td>                    
                        <td>${personality.description}</td>
                        <td>
                            <button type="button" class="update-button" onclick="updateCharacter(${personality.id})">更新</button>
                            <button type="button" class="delete-button" onclick="deleteCharacter(${personality.id})">删除</button>
                        </td>
                    </tr>
                `;
            });

            tableHtml += `
                    </tbody>
                </table>
            `;

            document.getElementById('character-table').innerHTML = tableHtml;
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

function fetchCategories() {
    fetch(`api/1.0/chat/routines?category=4`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
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

            currentData = filteredData;

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
    return currentData.find(question => question.id === id);
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
    document.getElementById('updateTypeModal').style.display = 'none';
    document.getElementById('updateCharacterModal').style.display = 'none';
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
            alert('刪除成功');
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
    let isAlerted = 0;
    let isAdded = 0;

    if (!selectedCategory) {
        alert("請選擇類別！");
        return;
    }
    //deal with input questions and answers
    for (let i = 0; i < questionAnswerPairs.length; i++) {
        const question = questionAnswerPairs[i].querySelector('input[name="questions[]"]').value.trim();
        const answer = questionAnswerPairs[i].querySelector('input[name="answers[]"]').value.trim();

        if (question === "" || answer === "") { //both answer and question should be input
            if (isAlerted === 0) {
                alert("請確保每一組問題和答案都已填寫");
            }
            isAlerted = 1;
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
                    if (isAdded === 0) {
                        alert('新增成功');
                    }
                    isAdded = 1;
                    handleCategoriesAndDisplayTable(typeId);
                    clearInputs('.question-answer-group');
                    return {};
                } else {
                    alert('新增失敗');
                    throw new Error('Network response was not ok');
                }
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

    createCategoryType(categoryName)
        .then(() => {
            document.getElementById('faq-category-name').value = '';
        });
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
                handleTypeAndDisplayTable();
                alert("新增成功");
                return {};
            } else {
                alert("新增失敗");
                throw new Error('Network response was not ok');
            }
        })
        .catch(error => console.error('Error:', error));
}

function handleTypeAndDisplayTable() {
    fetch(`api/1.0/admin/type/review`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('all-type-table').classList.remove('hidden');
            const filteredData = data.data;
            console.log(filteredData.length);

            if (filteredData.length === 0) {
                document.getElementById('all-type-table').classList.add('hidden');
                return;
            }

            currentData = filteredData;

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

let selectedTypeId;

function updateType(typeId) {
    selectedTypeId = typeId;
    const typeData = findTypeById(typeId);

    document.getElementById('type-input').value = typeData.type_name;

    document.getElementById('updateTypeModal').style.display = 'block';
}

function findTypeById(id) {
    return currentData.find(type => type.id === id);
}

function saveType() {
    const updatedType = document.getElementById('type-input').value;

    fetch(`api/1.0/admin/type/update`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: selectedTypeId,
            type_name: updatedType
        })
    }).then(response => {
        if (response.ok) {
            alert('更新成功');
            closeModal();
            handleTypeAndDisplayTable();
        } else {
            alert('更新失敗');
        }
    }).catch(error => {
        console.error('Error updating question:', error);
    });

    closeModal();
}

function deleteType(typeId) {
    selectedTypeId = typeId;
    fetch(`api/1.0/admin/type/delete`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: selectedTypeId
        })
    }).then(response => {
        if (response.ok) {
            alert('刪除成功');
            closeModal();
            handleTypeAndDisplayTable();
        } else {
            alert('刪除失敗');
        }
    }).catch(error => {
        console.error('Error updating question:', error);
    });

}

function submitGeneral() {
    const characterName = document.getElementById('character-name').value;

    if (!characterName) {
        alert('請輸入角色描述');
        return;
    }

    createCharacterType(characterName)
        .then(() => {
            document.getElementById('character-name').value = '';
        });
}

function createCharacterType(characterName) {
    if (characterName.length > 200) {
        alert('字數過長: ' + characterName.length + '字 (最多200字）');
        return;
    }

    return fetch('api/1.0/admin/personality/create', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            description: characterName
        })
    })
        .then(response => {
            if (response.ok) {
                fetchCharacterAndDisplayTable();
                alert('新增成功');
                return {};
            } else {
                alert('新增失敗');
                throw new Error('Network response was not ok');
            }
        })
        .catch(error => console.error('Error:', error));
}

let selectedCharId;

function updateCharacter(charId) {
    selectedCharId = charId;
    const charData = findCharById(charId);

    document.getElementById('character-input').value = charData.description;

    document.getElementById('updateCharacterModal').style.display = 'block';
}

function findCharById(id) {
    return currentData.find(char => char.id === id);
}

function saveCharacter() {
    const updatedChar = document.getElementById('character-input').value;

    if (updatedChar === '') {
        alert('角色描述不能為空');
        return;
    }

    if (updatedChar.length > 200) {
        alert('字數過長: ' + updatedChar.length + '字 (最多200字）');
        return;
    }

    fetch(`api/1.0/admin/personality/update`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: selectedCharId,
            description: updatedChar
        })
    }).then(response => {
        if (response.ok) {
            alert('更新成功');
            closeModal();
            fetchCharacterAndDisplayTable();
        } else {
            alert('更新失敗');
        }
    }).catch(error => {
        console.error('Error updating question:', error);
    });

    closeModal();
}

function deleteCharacter(charId) {
    selectedCharId = charId;
    fetch(`api/1.0/admin/personality/delete`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: selectedCharId
        })
    }).then(response => {
        if (response.ok) {
            alert('刪除成功');
            closeModal();
            fetchCharacterAndDisplayTable();
        } else {
            alert('刪除失敗');
        }
    }).catch(error => {
        console.error('Error updating question:', error);
    });

}

function solveJwt(token) {
    fetch(`api/1.0/user/solve-jwt?token=${token}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Failed to solve JWT');
                });
            }
            return response.json();
        })
        .then(data => {

            const userRole = data.role;

            if (userRole !== 'ADMIN') {
                window.location.href = '../../404.html';
            } else {
                console.log('User is an admin, proceeding...');
            }
        })
        .catch(error => {
            window.location.href = '../../404.html';
        });
}

function fetchSupportAndDisplayTable() {
    fetch(`api/1.0/admin/support/review`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('support-table').classList.remove('hidden');
            const filteredData = data.data;

            if (filteredData.length === 0) {
                document.getElementById('support-table').classList.add('hidden');
                return;
            }

            currentData = filteredData;

            let tableHtml = `
                <table class="styled-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>姓名</th>
                            <th>信箱</th>
                            <th>描述</th>
                            <th>時間</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            filteredData.forEach(support => {
                tableHtml += `
                    <tr>
                        <td>${support.id}</td>
                        <td>${support.name}</td>
                        <td>${support.email}</td>                   
                        <td>${support.description}</td>
                        <td>${support.request_time}</td>
                    </tr>
                `;
            });

            tableHtml += `
                    </tbody>
                </table>
            `;

            document.getElementById('support-table').innerHTML = tableHtml;
        })
        .catch(error => console.error('Error fetching categories:', error));
}

function removeQuestionAnswer() {
    const container = document.getElementById('question-answer-container');
    const questionAnswerGroups = container.getElementsByClassName('question-answer-group');

    if (questionAnswerGroups.length > 1) {
        container.removeChild(questionAnswerGroups[questionAnswerGroups.length - 1]);
    } else {
        alert('至少保留一組問題及答案');
    }

    checkCategorySelection();
}

function clearInputs(selector) {
    document.querySelectorAll(`${selector} input`).forEach(input => input.value = '');
}




