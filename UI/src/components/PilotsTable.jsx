import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, CircularProgress, IconButton, Chip, Tooltip, Box, Pagination,
    useMediaQuery, Typography, Stack, Select, MenuItem
} from '@mui/material';
import { Delete, Edit } from '@mui/icons-material';
import { useState, useEffect } from 'react';
import axios from 'axios';
import ConfirmationDialog from './ConfirmationDialog';
import PilotDialog from './PilotDialog';

export default function PilotsTable({ onError, onSuccess }) {
    const [pilots, setPilots] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editPilot, setEditPilot] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize, setPageSize] = useState(5);
    const isMobile = useMediaQuery('(max-width:600px)');

    const fetchPilots = async (pageNumber = page, size = pageSize) => {
        try {
            setLoading(true);
            const { data } = await axios.get(`/api/pilots?page=${pageNumber - 1}&size=${size}`);
            setPilots(data.content);
            setTotalPages(data.page.totalPages);
        } catch (error) {
            onError('Ошибка загрузки пилотов');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            await axios.delete(`/api/pilots/${deleteTarget}`);
            onSuccess('Пилот успешно удален');
            fetchPilots();
        } catch (error) {
            onError('Ошибка удаления пилота');
        }
        setDeleteTarget(null);
    };

    const handlePageChange = (event, value) => {
        setPage(value);
        fetchPilots(value);
    };
    useEffect(() => {
        fetchPilots();
    }, []);

    if (loading) return <CircularProgress sx={{ mt: 3 }} />;

    return (
        <Box sx={{
            height: 'calc(100vh - 200px)',
            position: 'relative',
            '& .MuiTableContainer-root': {
                backgroundColor: 'transparent important',
                boxShadow: 'none !important',
                height: '100%'
            }
        }}>
            <TableContainer component={Paper} sx={{
                background: 'rgba(40, 40, 40, 0.9)',
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
                        position: 'sticky',
                        top: 0, // Фиксация сверху
                        zIndex: 2, // Поверх скроллящегося контента
                        background: 'rgba(30, 30, 30, 0.8)',
                        borderBottom: '2px solid rgba(144, 202, 249, 0.2)',
                        backdropFilter: 'blur(12px)', // Добавляем размытие под шапкой
                        '& .MuiTableCell-root': {
                            color: '#90caf9 !important'
                        }
                    }}>
                        <TableRow>
                            <TableCell sx={{ color: 'black' }}>Имя</TableCell>
                            {!isMobile && <TableCell sx={{ color: 'black' }}>Автомобили</TableCell>}
                            <TableCell sx={{ color: 'black' }}>Действия</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {pilots.map(pilot => (
                            <TableRow key={pilot.id}>
                                <TableCell>
                                    <Box>
                                        <Typography variant="body1">{pilot.name}</Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Возраст: {pilot.age}, Опыт: {pilot.experience} лет
                                        </Typography>
                                        {isMobile && pilot.cars?.length > 0 && (
                                            <Stack direction="row" spacing={1} sx={{ mt: 1, flexWrap: 'wrap', gap: 0.5 }}>
                                                {pilot.cars.map(car => (
                                                    <Chip
                                                        key={car.id}
                                                        label={`${car.brand} ${car.model}`}
                                                        size="small"
                                                        color={black}
                                                        sx={{
                                                            background: 'rgba(0, 0, 0, 0.1)',
                                                            color: '#fff',
                                                            border: '1px solid rgba(255, 255, 255, 0.2)'
                                                        }}
                                                    />
                                                ))}
                                            </Stack>
                                        )}
                                    </Box>
                                </TableCell>

                                {!isMobile && (
                                    <TableCell>
                                        <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap', gap: 0.5 }}>
                                            {pilot.cars?.map(car => (
                                                <Chip
                                                    key={car.id}
                                                    label={`${car.brand} ${car.model}`}
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
                                )}

                                <TableCell>
                                    <Box sx={{ display: 'flex', gap: 1 }}>
                                        <Tooltip title="Редактировать">
                                            <IconButton
                                                onClick={() => setEditPilot(pilot)}
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
                                                onClick={() => setDeleteTarget(pilot.id)}
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
              position: 'fixed',
              bottom: 20,
              left: '50%', // Центрируем по горизонтали
              transform: 'translateX(-50%)', // Корректируем позицию
              maxWidth: 'calc(100vw - 50px)', // Устанавливаем ту же максимальную ширину, что и таблица
              width: '100%', // Занимаем всю доступную ширину контейнера
              zIndex: 2,
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
               background: 'rgba(0, 0, 0, 0.5)',
               backdropFilter: 'blur(8px)',
               borderTop: '1px solid rgba(255, 255, 255, 0.12)',
               boxShadow: '0 -4px 20px rgba(0,0,0,0.2)',
             }}>
                <Pagination
                    count={totalPages}
                    page={page}
                    onChange={handlePageChange}
                    sx={{
                        '& .MuiButtonBase-root': { color: theme => theme.palette.text.primary }
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
                        fetchPilots(1, newSize);
                    }}
                    size={isMobile ? 'small' : 'medium'}
                    sx={{
                        scale: 0.8,
                        ml: 2,
                        minWidth: 120,
                        '& .MuiSelect-icon': { color: theme => theme.palette.text.primary }
                    }}
                >
                    <MenuItem value={5}>5</MenuItem>
                    <MenuItem value={10}>10</MenuItem>
                    <MenuItem value={20}>20</MenuItem>
                </Select>
            </Box>

            <PilotDialog
                open={!!editPilot}
                onClose={() => setEditPilot(null)}
                pilotToEdit={editPilot}
                onError={onError}
                fetchPilots={fetchPilots}
                onSuccess={(msg) => {
                    onSuccess(msg);
                    fetchPilots();
                }}
            />

            <ConfirmationDialog
                open={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                onConfirm={async () => {
                    await handleDelete();
                    await fetchPilots();
                }}
                title="Удаление пилота"
                content="Вы уверены, что хотите удалить этого пилота?"
            />
        </Box>
    );
}