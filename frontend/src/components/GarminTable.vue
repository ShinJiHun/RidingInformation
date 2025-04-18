<template>
  <div class="table-wrapper">
    <h2>라이딩 기록</h2>
    <div v-if="loading">🚴 데이터를 불러오는 중입니다...</div>
    <table v-else class="ride-table">
      <thead>
        <tr class="table-row total-row">
          <th> 오늘 기준 </th>
          <th> 거리 (km) </th>
          <th> 고도 (m) </th>
          <th> 칼로리 (kcal) </th>
          <th> 시간 (분) </th>
        </tr>
      </thead>
      <tbody>
      <tr class="table-row total-row">
        <td>{{ formatFitDate(totalSummary.ridingDate) }}</td>
        <td>{{ totalSummary.distanceKm.toFixed(2) }}</td>
        <td>{{ totalSummary.altitude }}</td>
        <td>{{ totalSummary.calories }}</td>
        <td>
          {{ String(parseInt(totalSummary.durationMinutes / 60)).padStart(2, '0') }} 시간
          {{ String(totalSummary.durationMinutes % 60).padStart(2, '0') }} 분
        </td>
      </tr>
      </tbody>
      <thead>
        <tr>
          <th @click="sortBy('ridingDate')">라이딩 일시</th>
          <th @click="sortBy('distanceKm')">거리 (km)</th>
          <th @click="sortBy('altitude')">고도 (m)</th>
          <th @click="sortBy('calories')">칼로리 (kcal)</th>
          <th @click="sortBy('durationMinutes')">시간 (분)</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(item, index) in sortedList"
          :key="index"
          @click="selectFit(item)"
          class="table-row"
        >
          <td>{{ formatFitDate(item.ridingDate) }}</td>
          <td>{{ item.distanceKm.toFixed(2) }}</td>
          <td>{{ item.altitude || '-' }}</td>
          <td>{{ item.calories }}</td>
          <td>
            {{ String(parseInt(item.durationMinutes / 60)).padStart(2, '0') }} 시간
            {{ String(item.durationMinutes % 60).padStart(2, '0') }} 분
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 위치 정보 표시 영역 -->
    <div v-if="selectedFit" class="fit-detail">
      <h3>📍 {{ formatFitDate(selectedFit.ridingDate) }} 위치 정보</h3>
      <ul v-if="selectedFit.locations && selectedFit.locations.length">
        <li v-for="(loc, idx) in selectedFit.locations" :key="idx">
          위도: {{ loc.lat }}, 경도: {{ loc.lng }}
        </li>
      </ul>
      <p v-else>위치 정보가 없습니다.</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      fitList: [],
      loading: true,
      sortKey: 'ridingDate',
      sortAsc: true,
      selectedFit: null
    };
  },
  mounted() {
    fetch("http://localhost:8080/api/fit/files")
      .then(res => res.json())
      .then(data => {
        this.fitList = data;
        this.loading = false;
      })
        .catch(err => {
          console.error("❌ API 오류:", err);
          this.loading = false;
        });
  },
  computed: {
    sortedList() {
      return [...this.fitList].sort((a, b) => {
        let valA = a[this.sortKey];
        let valB = b[this.sortKey];

        if (this.sortKey === 'ridingDate') {
          valA = new Date(valA);
          valB = new Date(valB);
        }

        if (valA < valB) return this.sortAsc ? -1 : 1;
        if (valA > valB) return this.sortAsc ? 1 : -1;
        return 0;
      });
    },
    totalSummary() {
      const total = {
        ridingDate: new Date(), // 오늘 날짜
        distanceKm: 0,
        altitude: 0,
        calories: 0,
        durationMinutes: 0
      };

      this.fitList.forEach(item => {
        total.distanceKm += item.distanceKm || 0;
        total.altitude += item.altitude || 0;
        total.calories += item.calories || 0;
        total.durationMinutes += item.durationMinutes || 0;
      });

      return total;
    },
  },
  methods: {
    sortBy(key) {
      if (this.sortKey === key) {
        this.sortAsc = !this.sortAsc;
      } else {
        this.sortKey = key;
        this.sortAsc = true;
      }
    },
    formatFitDate(dateStr) {
      if (!dateStr) return '';

      const date = new Date(dateStr);
      const days = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      const dayOfWeek = days[date.getDay()];

      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds} ${dayOfWeek}`;
    },
    selectFit(item) {
      this.selectedFit = item;
    }
  }
};
</script>

<style scoped>
.table-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 30px;
}

.ride-table {
  border-collapse: collapse;
  width: 55%;
  font-size: 14px;
  table-layout: fixed;
  border: 3px solid #555; /* 🔷 전체 테이블 테두리 */
  border-radius: 6px;
}

th:nth-child(1) { width: 100px; }
th:nth-child(2) { width: 50px; }
th:nth-child(3) { width: 50px; }
th:nth-child(4) { width: 50px; }
th:nth-child(5) { width: 50px; }

th, td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: center;
  cursor: pointer;
}

th:hover {
  background-color: #f0f0f0;
}

.table-row:hover {
  background-color: #e7f5ff;
}

.total-row {
  background-color: #ffe8a1;
  font-weight: bold;
}

.total-row td, .total-row th {
  border-top: 3px solid #555;
  border-bottom: 3px solid #555;
  border-left: 3px solid #555;
  border-right: 3px solid #555;
}

.fit-detail {
  margin-top: 20px;
  width: 80%;
  background-color: #f9f9f9;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
}
</style>

