function toggleCategoryFields() {
    const categoryType = document.getElementById('category-type').value;
    document.getElementById('general-category-fields').classList.add('hidden');
    document.getElementById('faq-category-fields').classList.add('hidden');
    document.getElementById('questions-answers-section').classList.add('hidden');
    document.getElementById('submit-section').classList.add('hidden');

    if (categoryType === 'general') {
        document.getElementById('general-category-fields').classList.remove('hidden');
        document.getElementById('submit-section').classList.remove('hidden');
    } else if (categoryType === 'faq') {
        document.getElementById('faq-category-fields').classList.remove('hidden');
        document.getElementById('submit-section').classList.remove('hidden');
        document.getElementById('questions-answers-section').classList.remove('hidden');
    }
}

function showFaqSelect() {
    document.getElementById('faq-category-name').value = '';
    document.getElementById('faq-select-existing').classList.remove('hidden');
    document.getElementById('faq-add-new').classList.add('hidden');

    checkCategorySelection();
}

function showFaqAdd() {
    document.getElementById('faq-category-select').value = '';
    document.getElementById('faq-select-existing').classList.add('hidden');
    document.getElementById('faq-add-new').classList.remove('hidden');

    checkCategorySelection();
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

document.addEventListener("DOMContentLoaded", function() {
    fetchCategories();
});

function fetchCategories() {
    fetch('api/1.0/chat/routines?category=4')
        .then(response => response.json())
        .then(data => populateCategorySelect(data.data))
        .catch(error => console.error('Error fetching categories:', error));
}

function populateCategorySelect(categories) {
    const select = document.getElementById('faq-category-select');
    select.innerHTML = '<option value="">--請選擇--</option>';

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.type_name;
        select.appendChild(option);
    });
}

// function submitForm() {
//     const category = document.getElementById('general-category-name').value;
//     const url = document.getElementById('general-category-url').value;
//     const selectedCategory = document.getElementById('faq-category-select').value;
//     const questionAnswerPairs = document.querySelectorAll('.question-answer-group');
//     let payload = {};
//
//     const questions = [];
//     const answers = [];
//
//     //deal with input questions and answers
//     for (let i = 0; i < questionAnswerPairs.length; i++) {
//         const question = questionAnswerPairs[i].querySelector('input[name="questions[]"]').value.trim();
//         const answer = questionAnswerPairs[i].querySelector('input[name="answers[]"]').value.trim();
//
//         if (question === "" && answer === "") { //pass
//             continue;
//         } else if (question === "" || answer === "") { //both answer and question should be input
//             alert("請確保每一組問題和答案都已填寫");
//             return;
//         } else { //valid -> add
//             questions.push(question);
//             answers.push(answer);
//         }
//     }
//
//     if (category && url) {
//         payload = {
//             category: category,
//             url: url
//         };
//     } else if (selectedCategory) {
//         payload = {
//             type: selectedCategory,
//             questions: questions,
//             answers: answers
//         };
//     } else {
//         alert("請輸入類別及連結！");
//         return;
//     }
//
//     fetch('/api/submit', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify(payload)
//     })
//         .then(response => response.json())
//         .then(data => console.log('Success:', data))
//         .catch(error => console.error('Error:', error));
// }
