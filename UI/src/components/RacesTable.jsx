import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, CircularProgress, IconButton, Tooltip, Box, Alert, Pagination,
    useMediaQuery, Typography, Chip, Stack, Select, MenuItem
} from '@mui/material';
import { Delete, Edit } from '@mui/icons-material';
import { useState, useEffect } from 'react';
import axios from 'axios';
import ConfirmationDialog from './ConfirmationDialog';
import RaceDialog from './RaceDialog';

export default function RacesTable({ onError, onSuccess, relations, loadRelations }) {
    const [races, setRaces] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [editRace, setEditRace] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize, setPageSize] = useState(5);
    const isMobile = useMediaQuery('(max-width:600px)');

    const fetchRaces = async (pageNumber = page, size = pageSize) => {
        try {
            setError(null);
            setLoading(true);
            const { data } = await axios.get(`/api/races?page=${pageNumber - 1}&size=${size}`);
            setRaces(data.content || []);
            setTotalPages(data.page.totalPages);
        } catch (error) {
            setError('Не удалось загрузить данные');
            onError('Ошибка загрузки гонок');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            await axios.delete(`/api/races/${deleteTarget}`);
            onSuccess('Гонка успешно удалена');
            await fetchRaces();
        } catch (error) {
            onError('Ошибка удаления гонки');
        }
        setDeleteTarget(null);
    };

    const handlePageChange = (event, value) => {
        setPage(value);
        fetchRaces(value);
    };
    useEffect(() => {
        fetchRaces();
    }, []);

    if (loading) return <CircularProgress sx={{ mt: 3 }} />;

    return (
        <Box sx={{
            overflowX: 'auto',
            maxHeight: '70vh',
            overflowY: 'auto',
            position: 'relative',
            '& .MuiTableContainer-root': {
                backgroundColor: 'transparent !important',
                boxShadow: 'none !important'
            }
        }}>
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <TableContainer component={Paper} sx={{
                background: 'rgba(0, 0, 0, 0.3)',
                backdropFilter: 'blur(8px)',
                boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
                '&:hover': { boxShadow: '0 4px 20px rgba(0,0,0,0.08)' }
            }}>
                <Table
                    size={isMobile ? 'small' : 'medium'}
                    sx={{
                        '& .MuiTableRow-root': {
                            backgroundColor: 'rgba(0, 0, 0, 0.4)',
                            transition: 'background-color 0.3s'
                        },
                        '& .MuiTableRow-root:hover': {
                            backgroundColor: 'rgba(15, 15, 15, 0.7)'
                        }
                    }}
                >
                    <TableHead sx={{
                        background: 'rgba(144, 202, 249, 0.1)',
                        borderBottom: '2px solid rgba(144, 202, 249, 0.2)',
                        '& .MuiTableCell-root': {
                            color: '#90caf9 !important' // Цвет акцента
                        }
                    }}>
                        <TableRow>
                            <TableCell sx={{ color: 'black' }}>Гонка</TableCell>
                            <TableCell sx={{ color: 'black' }}>Участники</TableCell>
                            {!isMobile && <TableCell sx={{ color: 'black' }}>Автомобили</TableCell>}
                            <TableCell sx={{ color: 'black' }}>Действия</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {races.map(race => (
                            <TableRow key={race.id}>
                                <TableCell>
                                    <Box>
                                        <Typography variant="body1">
                                            {race.name || 'Без названия'}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Год: {race.year || '—'}
                                        </Typography>
                                    </Box>
                                </TableCell>

                                <TableCell>
                                    <Stack direction="row" spacing={0.5} sx={{ flexWrap: 'wrap' }}>
                                        {(race.pilots || []).map(pilot => (
                                            <Chip
                                                key={pilot?.id || 'unknown'}
                                                label={pilot?.name || 'Неизвестный пилот'}
                                                size="small"
                                                sx={{
                                                    background: 'rgba(63,81,181,0.1)',
                                                    backdropFilter: 'blur(4px)', // Добавьте размытие
                                                    border: '1px solid rgba(63,81,181,0.3)' // Полупрозрачная граница
                                                }}
                                            />
                                        ))}
                                    </Stack>
                                </TableCell>

                                {!isMobile && (
                                    <TableCell>
                                        <Stack direction="row" spacing={0.5} sx={{ flexWrap: 'wrap' }}>
                                            {(race.cars || []).map(car => (
                                                <Chip
                                                    key={car?.id || 'unknown'}
                                                    label={`${car?.brand || 'Без марки'} ${car?.model || 'Без модели'}`}
                                                    size="small"
                                                    sx={{
                                                        background: 'rgba(255, 255, 255, 0.1)',
                                                        color: '#fff',
                                                        border: '1px solid rgba(255, 255, 255, 0.2)'
                                                    }}
                                                />
                                            ))}
                                        </Stack>
                                    </TableCell>
                                )}

                                <TableCell>
                                    <Box sx={{ display: 'flex', gap: 1 }}>
                                        <Tooltip title="Редактировать">
                                            <IconButton
                                                onClick={() => {
                                                    loadRelations();
                                                    setEditRace(race);
                                                }}
                                                size={isMobile ? 'small' : 'medium'}
                                                sx={{
                                                    '&:hover svg': {
                                                        transform: 'rotate(15deg)',
                                                        transition: 'transform 0.3s'
                                                    }
                                                }}
                                            >
                                                <Edit fontSize={isMobile ? 'small' : 'medium'} />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Удалить">
                                            <IconButton
                                                onClick={() => setDeleteTarget(race.id)}
                                                color="error"
                                                size={isMobile ? 'small' : 'medium'}
                                                sx={{
                                                    '&:hover svg': {
                                                        transform: 'scale(1.2)',
                                                        transition: 'transform 0.3s'
                                                    }
                                                }}
                                            >
                                                <Delete fontSize={isMobile ? 'small' : 'medium'} />
                                            </IconButton>
                                        </Tooltip>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                mt: 2,
                p: 1,
                background: 'rgba(0,0,0,0.5)',
                borderRadius: 2,
                boxShadow: 1
            }}>
                <Pagination
                    count={totalPages}
                    page={page}
                    onChange={handlePageChange}
                    sx={{
                        '& .MuiButtonBase-root': {
                            color: theme => theme.palette.text.primary
                        }
                    }}
                    size={isMobile ? 'small' : 'medium'}
                    showFirstButton
                    showLastButton
                />

                <Select
                    value={pageSize}
                    onChange={(e) => {
                        const newSize = Number(e.target.value);
                        setPageSize(newSize);
                        setPage(1);
                        fetchRaces(1, newSize);
                    }}
                    size={isMobile ? 'small' : 'medium'}
                    sx={{
                        ml: 2,
                        minWidth: 120,
                        '& .MuiSelect-icon': {
                            color: theme => theme.palette.text.primary
                        }
                    }}                >
                    <MenuItem value={5}>5</MenuItem>
                    <MenuItem value={10}>10</MenuItem>
                    <MenuItem value={20}>20</MenuItem>
                </Select>
            </Box>

            <RaceDialog
                open={!!editRace}
                onClose={() => setEditRace(null)}
                raceToEdit={editRace}
                onError={onError}
                onSuccess={(msg) => {
                    onSuccess(msg);
                    fetchRaces();
                }}
                relations={relations}
            />

            <ConfirmationDialog
                open={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                onConfirm={handleDelete}
                title="Удаление гонки"
                content="Вы уверены, что хотите удалить эту гонку?"
            />
        </Box>
    );
}