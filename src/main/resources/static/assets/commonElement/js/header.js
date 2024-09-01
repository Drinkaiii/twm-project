/** Header */
function header() {
  // 產生透明圖層
  $("body").prepend('<div class="overlay"></div>');
}
document.addEventListener('DOMContentLoaded', () => {
  // user-info
  document.querySelector('.icon-user').addEventListener('click', function() {
    var infoWindow = document.querySelector('header .user .info-window');
    if (infoWindow.style.display === 'block') {
      infoWindow.style.display = 'none';
    } else {
      infoWindow.style.display = 'block';
    }
  });

  // log-out button
  document.querySelector('.log-out-content').addEventListener('click',function (){
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('userInfo');
    window.location.href = '/account_login.html';
  });

  document.querySelector('.user-info').textContent = `用戶 : `+localStorage.getItem("userInfo");
})


