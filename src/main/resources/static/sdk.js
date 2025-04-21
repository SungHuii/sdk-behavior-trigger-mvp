(function () {
  'use strict';

  (function(window) {
    const SDK = {
      startTracking: function({ apiUrl, projectKey }) {
        const startTime = Date.now();

        window.addEventListener("beforeunload", () => {
          const stayTime = Date.now() - startTime;

          navigator.sendBeacon(apiUrl, JSON.stringify({
            projectKey,
            eventType: "stay_time",
            durationMs: stayTime,
            occurredAt: new Date().toISOString()
          }));
        });
      }
    };

    window.BehaviorSDK = SDK;
  })(window);

})();