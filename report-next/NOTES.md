# report-next — integration notes

- **Issue:** https://github.com/lsd-consulting/lsd-core/issues/356
- **Intent:** land the greenfield Vite/TS spike in-tree as a parallel package while PlantUML stays the default report path.
- **Gradle:** intentionally not included in `settings.gradle` / `build.gradle`. Existing Java CI must keep passing untouched.
- **Packaging:** undecided (lsd-core major vs sibling). Keep this tree self-contained so either path remains viable.
- **Next steps (not this PR):** ReportJson schema from domain events; optional `reportEngine=next` flag; virtualization for large diagrams; wire built assets into jar when packaging is chosen.
