# Resultados do Microbenchmark JMH

## Configuração

- **Framework:** JMH (Java Microbenchmark Harness)
- **Modo:** Average Time (`avgt`)
- **Unidade:** Nanosegundos por operação (`ns/op`)
- **Iterações de warmup:** 10 × 2s
- **Iterações de medição:** 10 × 2s
- **JVM:** Java 23

## Resultados

| Benchmark | Modo | Iterações | Score (ns/op) | Erro (±) |
|---|---|---|---|---|
| `Bench.queryJDBC` | avgt | 20 | 1870,176 | ± 50,862 |
| `Bench.queryReflection` | avgt | 20 | 2719,859 | ± 19,714 |
| `Bench.queryDynamic` | avgt | 20 | 2436,292 | ±  24,148 |

## Análise e Conclusão

Os resultados mostram que a implementação baseada em Dynamic Reflection já esta mais rapida que a implementação ad hoc em JDBC, e com um overhead menor que na versao de Reflcetion.

